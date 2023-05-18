package com.acun.storyapp.data

import com.acun.storyapp.data.remote.Resource
import com.acun.storyapp.data.remote.StoryAppService
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

class RepositoryImpl(private val api: StoryAppService): Repository {
    override fun register(name: String, email: String, password: String): Flow<Resource<SimpleResponse>> = flow {
        emit(Resource.Loading())
        try {
            val result = api.register(name, email, password)
            if (!result.error) {
                emit(Resource.Success(data = result))
            } else {
                emit(Resource.Error(message = result.message))
            }
        } catch (e: HttpException) {
            val err = Gson().fromJson(e.response()?.errorBody()?.string(), SimpleResponse::class.java)
            emit(Resource.Error(message = err.message))
        } catch (e: IOException) {
            emit(Resource.Error(message = e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    override fun login(email: String, password: String): Flow<Resource<LoginResponse.LoginResult>> = flow {
        emit(Resource.Loading())
        try {
            val result = api.login(email, password)
            if (!result.error) {
                emit(Resource.Success(data = result.loginResult))
            } else {
                emit(Resource.Error(message = result.message))
            }
        } catch (e: HttpException) {
            val err = Gson().fromJson(e.response()?.errorBody()?.string(), SimpleResponse::class.java)
            emit(Resource.Error(message = err.message))
        } catch (e: IOException) {
            emit(Resource.Error(message = e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    override fun getStories(token: String, page: Int, size: Int): Flow<Resource<List<StoriesResponse.Story>>> = flow {
        emit(Resource.Loading())
        try {
            val result = api.getStories(token, page, size)
            if (!result.error) {
                emit(Resource.Success(data = result.listStory))
            } else {
                emit(Resource.Error(message = result.message))
            }
        } catch (e: HttpException) {
            val err = Gson().fromJson(e.response()?.errorBody()?.string(), SimpleResponse::class.java)
            emit(Resource.Error(message = err.message))
        } catch (e: IOException) {
            emit(Resource.Error(message = e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    override fun getStory(token: String, id: String): Flow<Resource<StoryResponse.Story>> = flow {
        emit(Resource.Loading())
        try {
            val result = api.getStory(token, id)
            if (!result.error) {
                emit(Resource.Success(data = result.story))
            } else {
                emit(Resource.Error(message = result.message))
            }
        } catch (e: HttpException) {
            val err = Gson().fromJson(e.response()?.errorBody()?.string(), SimpleResponse::class.java)
            emit(Resource.Error(message = err.message))
        } catch (e: IOException) {
            emit(Resource.Error(message = e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    override fun postStory(token: String, file: MultipartBody.Part, description: RequestBody, lat: RequestBody, long: RequestBody): Flow<Resource<SimpleResponse>> = flow {
        emit(Resource.Loading())
        try {
            val result = api.postStory(
                token = token,
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
            val err = Gson().fromJson(e.response()?.errorBody()?.string(), SimpleResponse::class.java)
            emit(Resource.Error(message = err.message))
        } catch (e: IOException) {
            emit(Resource.Error(message = e.localizedMessage ?: "An unexpected error occurred"))
        }
    }
}