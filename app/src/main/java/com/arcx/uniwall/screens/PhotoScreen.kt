package com.arcx.uniwall.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.arcx.uniwall.components.DataPoint
import com.arcx.uniwall.components.TopPhotoBar
import com.arcx.uniwall.components.WallpaperOptions
import com.arcx.uniwall.components.setWallpaper
import com.arcx.uniwall.models.PhotoScreenViewModel
import com.arcx.uniwall.models.PhotoScreenViewState
import kotlinx.coroutines.CoroutineScope

@Composable
fun PhotoScreen(
    modifier: Modifier = Modifier,
    id: String,
    viewModel: PhotoScreenViewModel = hiltViewModel()
) {

    val state by viewModel.stateFlow.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchSinglePhoto(id)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        when (val viewState = state) {
            is PhotoScreenViewState.Error,
            PhotoScreenViewState.Loading -> {
                LinearProgressIndicator()
            }

            is PhotoScreenViewState.Success -> {

                val painter = rememberAsyncImagePainter(model = viewState.photo.urls["regular"])
                val painterState by painter.state.collectAsState()
                when (painterState) {
                    AsyncImagePainter.State.Empty,
                    is AsyncImagePainter.State.Error,
                    is AsyncImagePainter.State.Loading -> {
                        LinearProgressIndicator()
                    }

                    is AsyncImagePainter.State.Success -> {

                        Image(
                            painter = painter,
                            contentDescription = viewState.photo.description,
                            contentScale = ContentScale.FillHeight,
                            modifier = Modifier
                                .fillMaxHeight()
                                .horizontalScroll(rememberScrollState())
                        )

                        TopPhotoBar(
                            photo = viewState.photo,
                            modifier = Modifier.align(Alignment.TopStart)
                        )

                        DataPoint(
                            userName = viewState.photo.user.name,
                            userId = viewState.photo.user.userName,
                            likes = viewState.photo.likes,
                            modifier = Modifier.align(Alignment.BottomStart)
                        )
                    }
                }
            }
        }

    }
}