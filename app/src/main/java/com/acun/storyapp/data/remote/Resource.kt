package com.acun.storyapp.data.remote

sealed class Resource<T>(val data: T? = null, val message: String = "") {
    class Loading<T> : Resource<T>()
    class Success<T>(data: T? = null): Resource<T>(data = data)
    class Error<T>(message: String): Resource<T>(message = message)
}
