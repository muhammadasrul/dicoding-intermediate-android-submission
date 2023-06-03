package com.acun.storyapp.ui.screen.stories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.acun.storyapp.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoriesViewModel @Inject constructor(private val repo: Repository) : ViewModel() {

    fun getStories(token: String) =
        repo.getStories(token)
            .asLiveData()
            .cachedIn(viewModelScope)
}