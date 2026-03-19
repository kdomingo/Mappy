package com.tourist.mappy.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Locale
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideApiKeyProvider(context: Context): ApiKeyProvider {
        return AppApiKeyProvider(context = context)
    }

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun providePlacesClient(context: Context, apiKeyProvider: ApiKeyProvider): PlacesClient {
        // Ensure Places is initialized. In a real app, this should be done in your Application class.
        // We'll initialize it here for simplicity, but it needs an API Key.
        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(context, apiKeyProvider.placesApiKey(), Locale.getDefault())
        }
        return Places.createClient(context)
    }
}
