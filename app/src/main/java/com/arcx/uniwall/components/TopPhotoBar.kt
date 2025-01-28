package com.arcx.uniwall.components

import android.app.WallpaperManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.Bitmap
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.toBitmap
import com.arcx.network.domain.Photo
import com.arcx.uniwall.models.TopBarViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

enum class WallpaperOptions {
    HOME_ONLY,
    LOCK_ONLY,
    BOTH
}

@Composable
fun TopPhotoBar(modifier: Modifier = Modifier, photo: Photo, viewModel: TopBarViewModel = hiltViewModel()) {

    val context = LocalContext.current.applicationContext

    Column(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Black, Color.Black)
                ), alpha = 0.3f
            )
            .padding(horizontal = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp)
                .height(48.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxHeight().clickable { viewModel.enableOrDisableWallpaper() }
            ) {
                Icon(Icons.Filled.Image, "Set Wallpaper", tint = Color.White)
                Spacer(Modifier.width(6.dp))
                Text("Set Wallpaper", color = Color.White)
            }
            Icon(Icons.Filled.Download,
                "Download Wallpaper",
                tint = Color.White,
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable {
//                        viewModel.downloadPhotoHotLink(photo)
                    }
            )
        }
        DropDownWallpaper(context, photo)
    }
}

@Composable
fun DropDownWallpaper(context: Context, photo: Photo, viewModel: TopBarViewModel = hiltViewModel()) {

    val options = listOf(
        "Home Only" to WallpaperOptions.HOME_ONLY,
        "Lock Only" to WallpaperOptions.LOCK_ONLY,
        "Both Home and Lock" to WallpaperOptions.BOTH)

    DropdownMenu(
        expanded = viewModel.wallpaperEnabled,
        onDismissRequest = {
            viewModel.enableOrDisableWallpaper()
        }
    ) {
        options.forEach { option ->
            DropdownMenuItem(
                text = {
                    Text(option.first)
                },
                onClick = {
                    viewModel.setWallpaper(context, photo, option.second)
                    viewModel.enableOrDisableWallpaper()
                }
            )
        }
    }
}