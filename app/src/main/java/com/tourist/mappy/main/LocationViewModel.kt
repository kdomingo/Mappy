package com.tourist.mappy.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.tourist.mappy.service.LocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationService: LocationService,
) : ViewModel() {

    private val _shouldRequestPermission = MutableStateFlow(false)
    val shouldRequestPermission = _shouldRequestPermission.asStateFlow()

    fun startLocationUpdates(context: Context) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
            return
        }

        locationService.start()
    }

    fun stopLocationUpdates() {
        locationService.stop()
    }

    private fun requestPermission() {
        _shouldRequestPermission.update { true }
    }
}