package com.arcx.network

import com.arcx.network.domain.Photo
import com.arcx.network.models.RemotePhoto
import com.arcx.network.models.RemoteSearchedPhoto
import com.arcx.network.models.toDomainPhoto
import com.arcx.network.models.toDomainSearchedPhoto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.get
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class KtorClient {

    private val clientKey = BuildConfig.ACCESS_KEY
    private val defaultDispatcher = Dispatchers.IO
    private val client = HttpClient(CIO) {
        defaultRequest { url("https://api.unsplash.com/") }

        install(Logging) {
            logger = Logger.SIMPLE
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    private var photosCache = mutableMapOf<String, Map<Int, List<Photo>>>()
    private var photoCache = mutableMapOf<String, Photo>()

    suspend fun fetchPhotos(criteria: String, page: Int): ApiOperation<List<Photo>> {
        if (criteria != "no_query") return searchPhotos(criteria, page)

        photosCache["no_query"]?.get(page)?.let { ApiOperation.Success(it) }
        return safeApiCall {
            println("Calling First time")
            withContext(defaultDispatcher) {
                client.get {
                    url {
                        path("photos")
                        parameters.append("client_id", clientKey)
                        parameters.append("page", "$page")
                        parameters.append("per_page", "30")
                    }
                }
                    .body<List<RemotePhoto>>()
                    .map { it.toDomainPhoto() }
                    .filter { !photoCache.contains(it.id) }
                    .also {
                        it.forEach { photoCache[it.id] = it }
                        photosCache["no_query"] = mapOf(page to it) }
            }
        }
    }

    fun fetchSinglePhoto(id: String): ApiOperation<Photo> {
        photoCache[id]!!.let { return ApiOperation.Success(it) }
    }

    suspend fun searchPhotos(criteria: String, page: Int): ApiOperation<List<Photo>> {
        photosCache[criteria]?.get(page)?.let { return ApiOperation.Success(it) }
        return safeApiCall {
             withContext(defaultDispatcher) {
                client.get {
                    url {
                        path("search/photos")
                        parameters.append("client_id", clientKey)
                        parameters.append("query", criteria)
                        parameters.append("page", "$page")
                        parameters.append("per_page", "30")
                        parameters.append("orientation", "portrait")
                    }
                }
            }

                .body<RemoteSearchedPhoto>()
                .toDomainSearchedPhoto().result
                .also {
                    it.forEach { photoCache[it.id] = it }
                    photosCache[criteria] = mapOf(page to it) }
        }
    }

    suspend fun downloadPhoto(id: String) {
        withContext(defaultDispatcher) {
            safeApiCall {
                client.get {
                    url {
                        path("photos/$id/download")
                        parameters.append("client_id", clientKey)
                    }
                }
            }
        }
    }

    private inline fun <T> safeApiCall(apiCall: () -> T): ApiOperation<T> {
        return try {
            ApiOperation.Success(data = apiCall())
        } catch (e: Exception) {
            ApiOperation.Fail(exception = e)
        }
    }
}

sealed interface ApiOperation<T> {
    data class Success<T>(val data: T): ApiOperation<T>
    data class Fail<T>(val exception: Exception): ApiOperation<T>

    fun onSuccess(block: (T) -> Unit): ApiOperation<T> {
        if (this is Success) block(data)
        return this
    }

    fun onFail(block: (Exception) -> Unit): ApiOperation<T> {
        if (this is Fail) block(exception)
        return this
    }
}