package com.acun.storyapp.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.acun.storyapp.data.local.StoryDatabase
import com.acun.storyapp.data.local.entity.StoryEntity
import com.acun.storyapp.data.remote.Resource
import com.acun.storyapp.data.remote.StoryApiService
import com.acun.storyapp.data.remote.response.LoginResponse
import com.acun.storyapp.data.remote.response.SimpleResponse
import com.acun.storyapp.data.remote.response.StoriesResponse
import com.acun.storyapp.data.remote.response.StoryResponse
import com.acun.storyapp.repository.Repository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException

class RepositoryImpl(
    private val apiService: StoryApiService,
    private val db: StoryDatabase
) : Repository {
    override fun register(
        name: String,
        email: String,
        password: String
    ): Flow<Resource<SimpleResponse>> = flow {
        emit(Resource.Loading())
        try {
            val result = apiService.register(name, email, password)
            if (!result.error) {
                emit(Resource.Success(data = result))
            } else {
                emit(Resource.Error(message = result.message))
            }
        } catch (e: HttpException) {
            val err =
                Gson().fromJson(e.response()?.errorBody()?.string(), SimpleResponse::class.java)
            emit(Resource.Error(message = err.message))
        } catch (e: IOException) {
            emit(Resource.Error(message = e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    override fun login(email: String, password: String): Flow<Resource<LoginResponse.LoginResult>> =
        flow {
            emit(Resource.Loading())
            try {
                val result = apiService.login(email, password)
                if (!result.error) {
                    emit(Resource.Success(data = result.loginResult))
                } else {
                    emit(Resource.Error(message = result.message))
                }
            } catch (e: HttpException) {
                val err =
                    Gson().fromJson(e.response()?.errorBody()?.string(), SimpleResponse::class.java)
                emit(Resource.Error(message = err.message))
            } catch (e: IOException) {
                emit(Resource.Error(message = e.localizedMessage ?: "An unexpected error occurred"))
            }
        }

    @OptIn(ExperimentalPagingApi::class)
    override fun getStories(token: String, isAlwaysFromNetwork: Boolean): Flow<PagingData<StoryEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                if (isAlwaysFromNetwork) StoryPagingSource(apiService, token)
                else db.storyDao().getStories()
            },
            remoteMediator = StoryMediator(token, apiService, db)
        ).flow
    }

    override fun getStoriesWithLocation(token: String): Flow<Resource<List<StoriesResponse.Story>>> =
        flow {
            emit(Resource.Loading())
            try {
                val result = apiService.getStoriesWithLocation("Bearer $token", 1)
                if (!result.error) {
                    emit(Resource.Success(data = result.listStory))
                } else {
                    emit(Resource.Error(message = result.message))
                }
            } catch (e: HttpException) {
                val err =
                    Gson().fromJson(e.response()?.errorBody()?.string(), SimpleResponse::class.java)
                emit(Resource.Error(message = err.message))
            } catch (e: IOException) {
                emit(Resource.Error(message = e.localizedMessage ?: "An unexpected error occurred"))
            }
        }

    override fun getStory(token: String, id: String): Flow<Resource<StoryResponse.Story>> = flow {
        emit(Resource.Loading())
        try {
            val result = apiService.getStory("Bearer $token", id)
            if (!result.error) {
                emit(Resource.Success(data = result.story))
            } else {
                emit(Resource.Error(message = result.message))
            }
        } catch (e: HttpException) {
            val err =
                Gson().fromJson(e.response()?.errorBody()?.string(), SimpleResponse::class.java)
            emit(Resource.Error(message = err.message))
        } catch (e: IOException) {
            emit(Resource.Error(message = e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    override fun postStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody,
        long: RequestBody
    ): Flow<Resource<SimpleResponse>> = flow {
        emit(Resource.Loading())
        try {
            val result = apiService.postStory(
                token = "Bearer $token",
                file = file,
                description = description,
                lat = lat,
                long = long
            )
            if (!result.error) {
                emit(Resource.Success(data = result))
            } else {
                emit(Resource.Error(message = result.message))
            }
        } catch (e: HttpException) {
            val err =
                Gson().fromJson(e.response()?.errorBody()?.string(), SimpleResponse::class.java)
            emit(Resource.Error(message = err.message))
        } catch (e: IOException) {
            emit(Resource.Error(message = e.localizedMessage ?: "An unexpected error occurred"))
        }
    }
}