package com.acun.storyapp.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.acun.storyapp.data.local.StoryDatabase
import com.acun.storyapp.data.local.entity.RemoteKeys
import com.acun.storyapp.data.local.entity.StoryEntity
import com.acun.storyapp.data.remote.StoryApiService
import com.acun.storyapp.data.remote.response.toEntity

@OptIn(ExperimentalPagingApi::class)
class StoryMediator(
    private val token: String,
    private val apiService: StoryApiService,
    private val db: StoryDatabase
): RemoteMediator<Int, StoryEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeysForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeysForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val response = apiService.getStories("Bearer $token", page, state.config.pageSize)
            val endOfPaginationReached = response.listStory.isEmpty()
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.remoteKeysDao().deleteRemoteKeys()
                    db.storyDao().deleteAll()
                }

                val prevKey = if (page == 1) null else -1
                val nextKey = if (endOfPaginationReached) null else page+1
                val keys = response.listStory.map {
                    RemoteKeys(it.id, prevKey, nextKey)
                }

                db.remoteKeysDao().insertAll(keys)
                db.storyDao().insertStory(
                    response.listStory.map { it.toEntity() }
                )
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeysForLastItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let {
            db.remoteKeysDao().getRemoteKeysId(it.id)
        }
    }

    private suspend fun getRemoteKeysForFirstItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let {
            db.remoteKeysDao().getRemoteKeysId(it.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.anchorPosition?.let { pos ->
            state.closestItemToPosition(pos)?.id?.let { id ->
                db.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }


    companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

}