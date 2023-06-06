package com.acun.storyapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.tokenPref: DataStore<Preferences> by preferencesDataStore("story_pref")
class AppDataStore(private val context: Context) {

    suspend fun setUserToken(token: String) {
        context.tokenPref.edit {
            it[TOKEN] = token
        }
    }

    suspend fun deleteUserToken() {
        context.tokenPref.edit {
            it.clear()
        }
    }

    val userToken = context.tokenPref.data.map {
        it[TOKEN].orEmpty()
    }

    companion object {
        private val TOKEN = stringPreferencesKey("token")
    }
}