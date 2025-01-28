package com.arcx.uniwall.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcx.network.domain.Photo
import com.arcx.uniwall.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val photoRepository: PhotoRepository
): ViewModel() {
    private val _viewState = MutableStateFlow<MainScreenViewState>(
        MainScreenViewState.isLoading
    )
    val viewState = _viewState.asStateFlow()
    private val fetchedPhotosPage = mutableMapOf<String, MutableSet<Photo>>()
    private val pageIndex = mutableMapOf<String, MutableList<Int>>()
    private val _searchState = MutableStateFlow("")
    val searchState = _searchState.asStateFlow()
    lateinit var otherCriteria: String

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    fun updateTextField(string: String) {
        _searchState.update { string }
        _searchState
            .debounce(500)
            .mapLatest { fetchInitialPage(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = ""
            )
    }

    fun fetchInitialPage(criteria: String) = viewModelScope.launch {
        if (criteria.isEmpty())
            otherCriteria = "no_query"
        else
            otherCriteria = criteria
        fetchedPhotosPage[otherCriteria]?.let { photos ->
            _viewState.update {
                return@update MainScreenViewState.Success(photos.toList())
            }
            return@launch
        }
        val initialPage = photoRepository.fetchPhotos(otherCriteria, 1)
        println("Fetching $otherCriteria")
        initialPage.onSuccess { photos ->
            if (fetchedPhotosPage[otherCriteria] == null) {
                fetchedPhotosPage[otherCriteria] = mutableSetOf()
                pageIndex[otherCriteria] = mutableListOf()
            }
            photos.forEach {
                fetchedPhotosPage[otherCriteria]?.add(it)
            }
            pageIndex[otherCriteria]?.add(1)
            _viewState.update { return@update MainScreenViewState.Success(photos) }
        }.onFail {
            // todo
        }
        println("Map is: $fetchedPhotosPage")
    }

    fun fetchNextPage() = viewModelScope.launch {
        val nextPageIndex = pageIndex[otherCriteria]?.last()?.inc()
        println("Next Page Index is: $nextPageIndex")
        if (nextPageIndex != null) {
            pageIndex[otherCriteria]?.add(nextPageIndex)
            photoRepository.fetchPhotos(otherCriteria, nextPageIndex).onSuccess { newPhotos ->
                newPhotos.forEach {
                    println("Storing newPhotos")
                    fetchedPhotosPage[otherCriteria]?.add(it)
                    println("Map is: ${fetchedPhotosPage[otherCriteria]?.size}")
                }
                _viewState.update { currentState ->
                    val currentPhotos = (currentState as MainScreenViewState.Success).photos
                    return@update MainScreenViewState.Success(photos = currentPhotos + newPhotos)
                }
            }.onFail {
                println("Failes")
                // todo
            }
        }

    }
}

sealed interface MainScreenViewState {
    object isLoading: MainScreenViewState
    data class Success(val photos: List<Photo> = listOf()): MainScreenViewState
}
