package com.tourist.mappy.map

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.tourist.mappy.data.MapData
import com.tourist.mappy.service.LocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val placesClient: PlacesClient,
    private val locationService: LocationService
) : ViewModel() {

    private val _state = MutableStateFlow(MapUiState())
    val state = _state.asStateFlow()

    private val placeId = savedStateHandle.toRoute<MapData>().arg

    private val locationCallback = object: LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            p0.lastLocation?.let {
                markCurrentLocation(LatLng(it.latitude, it.longitude))
            }
        }
    }

    init {
        locationService.listen(locationCallback)
    }

    @SuppressLint("MissingPermission")
    fun fetchCurrentLocation() {
        placeId?.let { id ->
            locationService.remove(locationCallback)
            val request = FetchPlaceRequest.newInstance(id, listOf(
                Place.Field.DISPLAY_NAME,
                Place.Field.FORMATTED_ADDRESS,
                Place.Field.LOCATION
            ))
            placesClient
                .fetchPlace(request)
                .addOnSuccessListener { response ->
                    val place = response.place
                    place.location?.let { coords ->
                        _state.update {
                            it.copy(
                                place = place,
                                currentLocation = LatLng(coords.latitude, coords.longitude)
                            )
                        }
                    }
                }
                .addOnFailureListener {
                    Log.d(javaClass.simpleName, "fetchCurrentLocation: ${it.message}")
                    locationService.fetchCurrentLocation()
                }

        } ?: locationService.fetchCurrentLocation()
    }

    private fun markCurrentLocation(location: LatLng) {
        _state.update {
            it.copy(
                currentLocation = location,
            )
        }
    }
}
