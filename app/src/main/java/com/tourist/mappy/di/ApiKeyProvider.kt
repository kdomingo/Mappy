package com.tourist.mappy.di

interface ApiKeyProvider {
    fun mapsApiKey(): String
    fun placesApiKey(): String
}

