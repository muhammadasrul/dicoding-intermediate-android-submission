package com.acun.storyapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.acun.storyapp.data.local.entity.StoryEntity
import com.acun.storyapp.data.remote.StoryApiService
import com.acun.storyapp.data.remote.response.toEntity

class StoryPagingSource (
    private val apiService: StoryApiService,
    private val token: String
): PagingSource<Int, StoryEntity>() {

    override fun getRefreshKey(state: PagingState<Int, StoryEntity>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryEntity> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val response = apiService.getStories("Bearer $token", position, params.loadSize)

            LoadResult.Page(
                data = response.listStory.map { it.toEntity() },
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position-1,
                nextKey = if (response.listStory.isEmpty()) null else position+1
            )
        } catch (err: Exception) {
            return LoadResult.Error(err)
        }
    }

    companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

}