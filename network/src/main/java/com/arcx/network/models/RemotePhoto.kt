package com.arcx.network.models

import com.arcx.network.domain.Photo
import com.arcx.network.domain.SearchedPhoto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemotePhoto(
    val id: String,
    val likes: Int,
    val urls: Map<String, String>,
    val description: String?,
    val user: User
) {
    @Serializable
    data class User(
        val name: String,
        @SerialName("username") val userName: String
    )
}

@Serializable
data class RemoteSearchedPhoto(
    @SerialName("total_pages") val totalPages: Int,
    val results: List<RemotePhoto>
)

fun RemotePhoto.toDomainPhoto(): Photo {
    return Photo(
        id = id,
        likes = likes,
        urls = urls,
        description = description,
        user = Photo.User(
            name = user.name,
            userName = user.userName
        )
    )
}

fun RemoteSearchedPhoto.toDomainSearchedPhoto(): SearchedPhoto {
    return SearchedPhoto(
        totalPages = totalPages,
        result = results.map { it.toDomainPhoto() }
    )
}