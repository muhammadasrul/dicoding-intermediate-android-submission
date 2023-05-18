package com.acun.storyapp.ui.screen.stories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acun.storyapp.data.remote.Resource
import com.acun.storyapp.data.remote.response.StoriesResponse
import com.acun.storyapp.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoriesViewModel @Inject constructor(
    private val repo: Repository,
) : ViewModel() {

    private val _storyState = MutableLiveData<Resource<List<StoriesResponse.Story>>>()
    val storyState: LiveData<Resource<List<StoriesResponse.Story>>> = _storyState

    fun getStories(token: String, page: Int, size: Int) {
        _storyState.postValue(Resource.Loading())
        viewModelScope.launch {
            repo.getStories(token = "Bearer $token", page = page, size = size)
                .collect {
                    _storyState.postValue(it)
                }
        }
    }
}