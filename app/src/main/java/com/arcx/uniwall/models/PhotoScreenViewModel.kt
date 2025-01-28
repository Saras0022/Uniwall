package com.arcx.uniwall.models

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.Bitmap
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.toBitmap
import com.arcx.network.domain.Photo
import com.arcx.uniwall.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoScreenViewModel @Inject constructor(
    private val photoRepository: PhotoRepository
): ViewModel() {

    private val _internalStorageFlow = MutableStateFlow<PhotoScreenViewState>(
        PhotoScreenViewState.Loading
    )
    val stateFlow = _internalStorageFlow.asStateFlow()

    fun fetchSinglePhoto(id: String) = viewModelScope.launch {
        _internalStorageFlow.update { return@update PhotoScreenViewState.Loading }
        photoRepository.fetchSinglePhoto(id).onSuccess { photo ->
            _internalStorageFlow.update {
                return@update PhotoScreenViewState.Success(
                    photo = photo
                )
            }
        }.onFail { exception ->
            _internalStorageFlow.update {
                return@update PhotoScreenViewState.Error(
                    message = exception.message ?: "Unknown Error Occurred"
                )
            }
        }
    }

    fun downloadPhoto(photo: Photo) {

    }

    fun imageRequest(context: Context, url: String): Bitmap? {
        var image: Bitmap? = null
        val loader = ImageLoader.Builder(context).build()
        val request = ImageRequest.Builder(context)
            .data(url)
            .build()
        try {
            val loadBitmap = viewModelScope.launch(Dispatchers.IO) {
                image = loader.execute(request).image?.toBitmap()
            }
            loadBitmap.invokeOnCompletion {
                image = image
            }
        } catch (e: Exception) {
            // todo
        }
        println("Image is: $image")
        return image
    }
}

sealed interface PhotoScreenViewState {
    object Loading: PhotoScreenViewState
    data class Error(val message: String): PhotoScreenViewState
    data class Success(
        val photo: Photo
    ): PhotoScreenViewState
}