package com.arcx.uniwall.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.arcx.uniwall.components.SearchBar
import com.arcx.uniwall.models.MainScreenViewModel
import com.arcx.uniwall.models.MainScreenViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@SuppressLint("FlowOperatorInvokedInComposition")
@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class, ExperimentalCoroutinesApi::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier,
               viewModel: MainScreenViewModel = hiltViewModel(),
               onClick: (String) -> Unit) {
    val state by viewModel.viewState.collectAsState()
    val scrollState = rememberLazyGridState()
    val isAtBottom by remember {
        derivedStateOf {
            val currentPhotoCount =
                (state as? MainScreenViewState.Success)?.photos?.size
                    ?: return@derivedStateOf false
            val lastDisplayedIndex = scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                ?: return@derivedStateOf false
            return@derivedStateOf lastDisplayedIndex >= currentPhotoCount - 10
        }
    }
    println(isAtBottom)
    val searchState by viewModel.searchState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchInitialPage(searchState)
    }

    LaunchedEffect(isAtBottom) {
        if (isAtBottom)
            viewModel.fetchNextPage()
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier.fillMaxSize()
    ) {

        SearchBar()

        Spacer(Modifier.height(6.dp))

        when(val viewState = state) {
            is MainScreenViewState.Success -> {
                LazyVerticalGrid(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(6.dp),
                    columns = GridCells.Fixed(2),
                    state = scrollState,
                ) {
                    items(viewState.photos) { photo ->

                        val painter = rememberAsyncImagePainter(photo.urls["small"])
                        val painterState by painter.state.collectAsState()

                        Box(
                            modifier = Modifier.sizeIn(minHeight = 256.dp)
                        ) {
                            when (painterState) {
                                AsyncImagePainter.State.Empty,
                                is AsyncImagePainter.State.Error,
                                is AsyncImagePainter.State.Loading -> {
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                                }
                                is AsyncImagePainter.State.Success -> {
                                    Image(painter,
                                        contentDescription = photo.description,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .aspectRatio(0.65f, true)
                                            .clickable { onClick(photo.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            MainScreenViewState.isLoading -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                } }
        }
    }
}