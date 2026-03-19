package com.tourist.mappy.service

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationService @Inject constructor(
    private val context: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) {
    private val callbacks = mutableListOf<LocationCallback>()

    private val callback = object: LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            callbacks.forEach { it.onLocationResult(p0) }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun start() {
        fetchCurrentLocation()
        val locationRequest = LocationRequest.Builder(1000L).build()
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, callback, context.mainLooper)
    }

    fun stop() {
        fusedLocationProviderClient.removeLocationUpdates(callback)
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun fetchCurrentLocation() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            callbacks.forEach { it.onLocationResult(LocationResult.create(listOf(location))) }
        }
    }

    fun listen(callback: LocationCallback) {
        callbacks.add(callback)
    }

    fun remove(callback: LocationCallback) {
        callbacks.remove(callback)
    }
}