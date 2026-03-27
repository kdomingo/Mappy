package com.tourist.mappy.map

import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place

data class MapUiState(
    val place: Place? = null,
    val placeName: String? = null,
    val currentLocation: LatLng? = null
)
