package com.acun.storyapp.ui.screen.stories.add_story

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acun.storyapp.data.remote.Resource
import com.acun.storyapp.data.remote.response.SimpleResponse
import com.acun.storyapp.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(private val repos: Repository) : ViewModel() {

    private var _file = MutableLiveData<Uri>()
    val file = _file

    private var _uploadState = MutableLiveData<Resource<SimpleResponse>>()
    val uploadState = _uploadState

    fun setFile(file: Uri) {
        _file.value = file
    }

    fun uploadFile(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody,
        long: RequestBody
    ) {
        viewModelScope.launch {
            repos.postStory(
                token = token,
                file = file,
                description = description,
                lat = lat,
                long = long
            ).collect {
                _uploadState.postValue(it)
            }
        }
    }
}