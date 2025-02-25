package com.arcx.uniwall.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arcx.uniwall.models.MainScreenViewModel

@Composable
fun SearchBar(modifier: Modifier = Modifier, viewModel: MainScreenViewModel = hiltViewModel()) {
    val searchState by viewModel.searchState.collectAsState()
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = searchState,
        onValueChange = { viewModel.updateTextField(it) },
        placeholder = {
            Text("Search Wallpapers")
        },
        leadingIcon = {
            Icon(Icons.Default.Search, "Search Icon")
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            viewModel.fetchInitialPage(searchState)
            focusManager.clearFocus()
        }),
        modifier = modifier
            .padding(horizontal = 6.dp)
            .fillMaxWidth()
    )
}