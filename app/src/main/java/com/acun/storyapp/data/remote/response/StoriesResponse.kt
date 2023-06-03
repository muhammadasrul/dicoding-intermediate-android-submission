package com.acun.storyapp.data.remote.response


import android.os.Parcelable
import com.acun.storyapp.data.local.entity.StoryEntity
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class StoriesResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("listStory")
    val listStory: List<Story>,
    @SerializedName("message")
    val message: String
) {
    @Parcelize
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
    ): Parcelable
}

fun StoriesResponse.Story.toEntity(): StoryEntity {
    return StoryEntity(
        createdAt = this.createdAt,
        description = this.description,
        id = this.id,
        lat = this.lat,
        lon = this.lon,
        name = this.name,
        photoUrl = this.photoUrl
    )
}