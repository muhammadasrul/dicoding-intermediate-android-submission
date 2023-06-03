package com.acun.storyapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.acun.storyapp.data.local.dao.RemoteKeysDao
import com.acun.storyapp.data.local.dao.StoryDao
import com.acun.storyapp.data.local.entity.RemoteKeys
import com.acun.storyapp.data.local.entity.StoryEntity

@Database(
    entities = [StoryEntity::class, RemoteKeys::class],
    version = 2,
    exportSchema = false
)
abstract class StoryDatabase: RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}