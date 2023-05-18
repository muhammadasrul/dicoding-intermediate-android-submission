package com.acun.storyapp.data.remote.response


import com.google.gson.annotations.SerializedName

data class SimpleResponse(
    @SerializedName("error")
    val error: Boolean = false,
    @SerializedName("message")
    val message: String = "",
    val isLoading: Boolean = false
)