package com.tourist.mappy.map

import com.google.android.gms.maps.model.LatLng

data class MapUiState(
    val currentLocation: LatLng? = null
)
