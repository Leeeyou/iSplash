package com.leeeyou.isplash.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.leeeyou.isplash.BuildConfig
import com.leeeyou.isplash.data.CollectionResponse
import com.leeeyou.isplash.data.PhotoDetail
import com.leeeyou.isplash.data.PhotoResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface UnsplashService {

    @Headers("Authorization:Client-ID ${BuildConfig.UNSPLASH_ACCESS_KEY}")
    @GET("photos")
    suspend fun fetchLatestPhoto(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): PhotoResponse

    @Headers("Authorization:Client-ID ${BuildConfig.UNSPLASH_ACCESS_KEY}")
    @GET("collections")
    suspend fun fetchCollections(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): CollectionResponse

    @Headers("Authorization:Client-ID ${BuildConfig.UNSPLASH_ACCESS_KEY}")
    @GET("/photos/{id}")
    suspend fun fetchPhotoDetail(@Path("id") id: String): PhotoDetail

    companion object {
        private const val BASE_URL = "https://api.unsplash.com/"

        fun create(): UnsplashService {
            val logger = HttpLoggingInterceptor().apply { level = Level.BASIC }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
                .build()
                .create(UnsplashService::class.java)
        }
    }
}
