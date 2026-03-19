package com.tourist.mappy.di

import android.content.Context
import com.tourist.mappy.R

class AppApiKeyProvider(
    private val context: Context
) : ApiKeyProvider {
    override fun mapsApiKey(): String = context.getString(R.string.maps_key)
    override fun placesApiKey(): String = context.getString(R.string.places_key)
}