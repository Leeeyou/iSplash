package com.leeeyou.isplash.repository

import com.leeeyou.isplash.api.UnsplashService
import com.leeeyou.isplash.data.CollectionResponse
import com.leeeyou.isplash.data.PhotoDetail
import com.leeeyou.isplash.data.PhotoResponse

class UnsplashRepository private constructor(private val service: UnsplashService) {

    companion object {
        val instance: UnsplashRepository by lazy {
            UnsplashRepository(UnsplashService.create())
        }
    }

    suspend fun getLatestPhoto(page: Int, perPage: Int): PhotoResponse {
        return service.fetchLatestPhoto(page, perPage)
    }

    suspend fun getCollections(page: Int, perPage: Int): CollectionResponse {
        return service.fetchCollections(page, perPage)
    }

    suspend fun getPhotoDetail(id: String): PhotoDetail {
        return service.fetchPhotoDetail(id)
    }

}
