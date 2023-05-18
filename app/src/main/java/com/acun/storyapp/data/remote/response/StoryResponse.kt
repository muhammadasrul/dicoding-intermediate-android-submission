package com.acun.storyapp.data.remote.response


import com.google.gson.annotations.SerializedName

data class StoryResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("story")
    val story: Story
) {
    data class Story(
        @SerializedName("createdAt")
        val createdAt: String,
        @SerializedName("description")
        val description: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("lat")
        val lat: Double,
        @SerializedName("lon")
        val lon: Double,
        @SerializedName("name")
        val name: String,
        @SerializedName("photoUrl")
        val photoUrl: String
    )
}