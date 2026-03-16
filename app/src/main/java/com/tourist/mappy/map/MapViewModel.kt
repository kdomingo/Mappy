package com.tourist.mappy.map

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {
    var isSearchDialogOpen by mutableStateOf(false)
        private set

    var searchQuery by mutableStateOf("")
        private set

    fun onSearchTapped() {
        isSearchDialogOpen = true
    }

    fun onDismissSearch() {
        isSearchDialogOpen = false
    }

    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
    }

    fun onSearchConfirm() {
        // Logic to handle search confirmation (e.g., geocoding)
        isSearchDialogOpen = false
    }
}
