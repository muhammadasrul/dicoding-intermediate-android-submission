package com.acun.storyapp.ui.screen.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acun.storyapp.data.remote.Resource
import com.acun.storyapp.data.remote.response.SimpleResponse
import com.acun.storyapp.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val repo: Repository): ViewModel() {

    private var _registerResultResult = MutableLiveData<Resource<SimpleResponse>>()
    val registerResult = _registerResultResult

    private var _isError = MutableLiveData(false)
    val isError = _isError

    fun setIsError(error: Boolean) {
        _isError.value = error
    }

    fun register(name: String, email: String, password: String) {
        _registerResultResult.postValue(Resource.Loading())
        viewModelScope.launch {
            repo.register(name, email, password).collect {
                _registerResultResult.postValue(it)
            }
        }
    }
}