package com.acun.storyapp.ui.screen.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acun.storyapp.data.remote.Resource
import com.acun.storyapp.data.remote.response.LoginResponse
import com.acun.storyapp.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repo: Repository): ViewModel() {

    private var _loginResult = MutableLiveData<Resource<LoginResponse.LoginResult>>()
    val loginResult = _loginResult

    private var _isError = MutableLiveData(false)
    val isError = _isError

    fun setIsError(error: Boolean) {
        _isError.value = error
    }

    fun login(email: String, password: String) {
        _loginResult.postValue(Resource.Loading())
        viewModelScope.launch {
            repo.login(email, password).collect {
                _loginResult.postValue(it)
            }
        }
    }
}