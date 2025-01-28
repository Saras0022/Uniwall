package com.arcx.uniwall.models

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.toBitmap
import com.arcx.network.domain.Photo
import com.arcx.uniwall.components.WallpaperOptions
import com.arcx.uniwall.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopBarViewModel @Inject constructor(
    private val photoRepository: PhotoRepository
): ViewModel() {

    var wallpaperEnabled by mutableStateOf(false)
        private set

    fun downloadPhotoHotLink(photo: Photo) {
        viewModelScope.launch {
            photoRepository.downloadPhoto(photo.id)
        }
    }

    fun enableOrDisableWallpaper() {
        wallpaperEnabled = !wallpaperEnabled
    }

    fun setWallpaper(context: Context, photo: Photo, wallpaperOptions: WallpaperOptions) {
        applyWallpaper(context, photo, wallpaperOptions)
    }

    private fun applyWallpaper(context: Context, photo: Photo, wallpaperOption: WallpaperOptions) {
        val wallpaperManager = WallpaperManager.getInstance(context.applicationContext)
        var bitmap: Bitmap? = null
        val loadBitmap = viewModelScope.launch(Dispatchers.IO) {
            val loader = ImageLoader.Builder(context).build()
            val request = ImageRequest.Builder(context)
                .data(photo.urls["regular"])
                .build()
            val result = loader.execute(request)
            bitmap = result.image?.toBitmap()
        }
        try {
            loadBitmap.invokeOnCompletion {
                when (wallpaperOption) {
                    WallpaperOptions.HOME_ONLY -> {
                        wallpaperManager.setBitmap(bitmap, null, false, WallpaperManager.FLAG_SYSTEM)
                    }
                    WallpaperOptions.LOCK_ONLY -> {
                        wallpaperManager.setBitmap(bitmap, null, false, WallpaperManager.FLAG_LOCK)
                    }
                    WallpaperOptions.BOTH -> {
                        wallpaperManager.setBitmap(bitmap, null, false)
                    }
                }
            }
            Toast.makeText(context, "Wallpaper Set", Toast.LENGTH_SHORT).show()
            downloadPhotoHotLink(photo)
        } catch (e: Exception) {
            // todo
        }
    }
}