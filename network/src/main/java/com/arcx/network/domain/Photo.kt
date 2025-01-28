package com.arcx.network.domain

data class Photo(
    val id: String,
    val likes: Int,
    val urls: Map<String, String>,
    val description: String?,
    val user: User
) {
    data class User(
        val name: String,
        val userName: String
    )
}

data class SearchedPhoto(
    val totalPages: Int,
    val result: List<Photo>
)