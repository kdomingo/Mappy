package com.tourist.mappy.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.tourist.mappy.R
import com.tourist.mappy.data.MapData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController
) {

    val viewModel = hiltViewModel<SearchViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val queryController = rememberTextFieldState()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = {
                keyboardController?.hide()
                focusManager.clearFocus(force = true)
            }
        ),
        topBar = {
            TopAppBar(
                title = {
                    Text("Mappy")
                },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate(MapData())
                        }
                    ) {
                        Icon(Icons.Filled.Map, stringResource(R.string.icon_description_map))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                state = queryController,
                lineLimits = TextFieldLineLimits.SingleLine,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = {
                    Text("Search places")
                },
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                ),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            keyboardController?.hide()
                            viewModel.onSearchQueryChanged(queryController.text.toString())
                        }
                    ) {
                        Icon(Icons.Filled.Search, stringResource(R.string.icon_description_search))
                    }
                },
                onKeyboardAction = {
                    keyboardController?.hide()
                    viewModel.onSearchQueryChanged(queryController.text.toString())
                }
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (uiState.error != null) {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                } else {
                    if (uiState.searchResults.isEmpty()) {
                        Text(
                            stringResource(R.string.search_placeholder),
                            fontWeight = FontWeight.W600,
                            fontSize = 18.sp,
                            modifier = Modifier.align(alignment = Alignment.Center)
                        )
                    }
                    LazyColumn {
                        items(uiState.searchResults) { prediction ->
                            SearchResultItem(
                                prediction = prediction,
                                onClick = {
                                    navController.navigate(MapData(arg = prediction.placeId))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    prediction: AutocompletePrediction,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(
            text = prediction.getPrimaryText(null).toString(),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.W600)
        )
        Text(
            text = prediction.getSecondaryText(null).toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}