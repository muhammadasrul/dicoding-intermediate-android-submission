package com.acun.storyapp.util

import com.acun.storyapp.data.local.entity.StoryEntity

object DataDummy {
    fun generateStoryDummy(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()

        for (i in 0..15) {
            val item = StoryEntity(
                createdAt = "",
                description = "description $i",
                id = "id$i",
                lat = 0.0,
                lon = 0.0,
                name = "name $i",
                photoUrl = ""
            )
            items.add(item)
        }
        return items
    }
}