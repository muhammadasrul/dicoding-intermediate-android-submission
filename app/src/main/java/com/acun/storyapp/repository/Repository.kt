package com.acun.storyapp.repository

import androidx.paging.PagingData
import com.acun.storyapp.data.local.entity.StoryEntity
import com.acun.storyapp.data.remote.Resource
import com.acun.storyapp.data.remote.response.LoginResponse
import com.acun.storyapp.data.remote.response.SimpleResponse
import com.acun.storyapp.data.remote.response.StoriesResponse
import com.acun.storyapp.data.remote.response.StoryResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface Repository {
    fun register(name: String, email: String, password: String): Flow<Resource<SimpleResponse>>
    fun login(email: String, password: String): Flow<Resource<LoginResponse.LoginResult>>
    fun getStories(token: String, isAlwaysFromNetwork: Boolean = false): Flow<PagingData<StoryEntity>>
    fun getStoriesWithLocation(token: String): Flow<Resource<List<StoriesResponse.Story>>>
    fun getStory(token: String, id: String): Flow<Resource<StoryResponse.Story>>
    fun postStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody,
        long: RequestBody
    ): Flow<Resource<SimpleResponse>>
}