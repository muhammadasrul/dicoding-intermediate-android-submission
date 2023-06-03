package com.acun.storyapp.di

import android.content.Context
import androidx.room.Room
import com.acun.storyapp.BuildConfig.BASE_URL
import com.acun.storyapp.data.RepositoryImpl
import com.acun.storyapp.data.local.StoryDatabase
import com.acun.storyapp.data.remote.StoryApiService
import com.acun.storyapp.repository.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): StoryApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(StoryApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): StoryDatabase {
        return Room.databaseBuilder(context, StoryDatabase::class.java, "story_db").build()
    }

    @Provides
    @Singleton
    fun provideRepository(apiService: StoryApiService, db: StoryDatabase): Repository {
        return RepositoryImpl(apiService, db)
    }
}