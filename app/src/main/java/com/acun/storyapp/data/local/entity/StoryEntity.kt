package com.acun.storyapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.acun.storyapp.data.remote.response.StoriesResponse

@Entity("story")
data class StoryEntity(
    val createdAt: String,
    val description: String,
    @PrimaryKey
    val id: String,
    val lat: Double,
    val lon: Double,
    val name: String,
    val photoUrl: String
)

fun StoryEntity.toResponse(): StoriesResponse.Story {
    return StoriesResponse.Story(
        createdAt = this.createdAt,
        description = this.description,
        id = this.id,
        lat = this.lat,
        lon = this.lon,
        name = this.name,
        photoUrl = this.photoUrl
    )
}
