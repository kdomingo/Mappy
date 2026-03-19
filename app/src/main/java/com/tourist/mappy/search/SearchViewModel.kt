package com.tourist.mappy.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.tourist.mappy.service.LocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val placesClient: PlacesClient,
    locationService: LocationService
) : ViewModel() {

    private var currentLocation: LatLng? = null

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            p0.lastLocation?.let {
                currentLocation = LatLng(it.latitude, it.longitude)
            }
        }
    }

    init {
        locationService.listen(callback = locationCallback)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        if (query.length >= 3) {
            searchJob = viewModelScope.launch {
                delay(500) // Debounce
                searchPlaces(query)
            }
        } else {
            _uiState.update { it.copy(searchResults = emptyList()) }
        }
    }

    private fun searchPlaces(query: String) {
        _uiState.update { it.copy(isLoading = true) }

        var bounds: CircularBounds? = null
        currentLocation?.let {
            bounds = CircularBounds.newInstance(it, 2000.0)
        }

        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .setOrigin(currentLocation)
            .setCountries("PH")
            .setLocationRestriction(bounds)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                _uiState.update {
                    it.copy(
                        searchResults = response.autocompletePredictions,
                        isLoading = false,
                        error = null
                    )
                }
            }
            .addOnFailureListener { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        searchResults = emptyList(),
                        error = exception.message ?: "Unknown error"
                    )
                }
            }
    }
}
