package com.tourist.mappy.main

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.*
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import androidx.navigation.navArgument
import com.google.android.gms.maps.model.LatLng
import com.tourist.mappy.Screens
import com.tourist.mappy.data.MapData
import com.tourist.mappy.map.MapScreen
import com.tourist.mappy.search.SearchScreen

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val viewModel = hiltViewModel<LocationViewModel>()
    val shouldRequestPermission by viewModel.shouldRequestPermission.collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                viewModel.startLocationUpdates(context)
            }
        }
    )

    LaunchedEffect(shouldRequestPermission) {
        if (shouldRequestPermission) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            Log.d(javaClass.simpleName, "MainScreen: Event: $event")
            when (event) {
                ON_START -> {
                    viewModel.startLocationUpdates(context)
                }

                ON_PAUSE, ON_STOP -> {
                    viewModel.stopLocationUpdates()
                }

                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val navController = rememberNavController()
    val navGraph = remember(navController) {
        navController.createGraph(startDestination = Screens.Search.name) {
            composable<MapData> {
                MapScreen(navController = navController)
            }

            composable(Screens.Search.name) {
                SearchScreen(navController = navController)
            }
        }
    }

    NavHost(navController = navController, navGraph)


//    NavHost(navController = navController, navGraph) {
//        composable(
//            Screens.Map.name
//        ) {
//            MapScreen(navController = navController)
//        }
//        composable(Screens.Search.name) {
//            SearchScreen(navController = navController)
//        }
//    }
}