package com.arcx.uniwall.repository

import com.arcx.network.ApiOperation
import com.arcx.network.KtorClient
import com.arcx.network.domain.Photo
import javax.inject.Inject

class PhotoRepository @Inject constructor(private val ktorClient: KtorClient) {
    suspend fun fetchPhotos(criteria: String, page: Int): ApiOperation<List<Photo>> {
        return ktorClient.fetchPhotos(criteria, page)
    }

    fun fetchSinglePhoto(id: String): ApiOperation<Photo> {
        return ktorClient.fetchSinglePhoto(id)
    }

    suspend fun downloadPhoto(id: String) {
        ktorClient.downloadPhoto(id)
    }
}