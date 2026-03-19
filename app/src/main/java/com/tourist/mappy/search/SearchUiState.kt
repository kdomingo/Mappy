package com.tourist.mappy.search

import com.google.android.libraries.places.api.model.AutocompletePrediction

data class SearchUiState(
    val searchQuery: String = "",
    val searchResults: List<AutocompletePrediction> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
