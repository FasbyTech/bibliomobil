package com.fasby.bibliomobil.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fasby.bibliomobil.add_volume.presentation.AddVolumeViewModel
import com.fasby.bibliomobil.add_volume.ui.AddVolumeScreen
import com.fasby.bibliomobil.camera.presentation.CameraOcrViewModel
import com.fasby.bibliomobil.camera.ui.CameraOcrScreen
import com.fasby.bibliomobil.catalog.presentation.CatalogViewModel
import com.fasby.bibliomobil.catalog.ui.CatalogScreen
import com.fasby.bibliomobil.collections.presentation.CollectionViewModel
import com.fasby.bibliomobil.collections.ui.CollectionScreen
import com.fasby.bibliomobil.settings.presentation.SettingsViewModel
import com.fasby.bibliomobil.settings.ui.SettingsScreen
import com.fasby.bibliomobil.volume_detail.presentation.VolumeDetailViewModel
import com.fasby.bibliomobil.volume_detail.ui.VolumeDetailScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Catalog,
        Screen.Collections,
        Screen.Scanner,
        Screen.AddVolume,
        Screen.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            val route = when (screen) {
                                is Screen.Catalog -> screen.createRoute(null)
                                is Screen.AddVolume -> screen.createRoute(null)
                                else -> screen.route
                            }
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Catalog.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(
                route = Screen.Catalog.route,
                arguments = listOf(navArgument("collectionId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                val collectionId = backStackEntry.arguments?.getString("collectionId")
                val viewModel: CatalogViewModel = hiltViewModel()
                
                LaunchedEffect(collectionId) {
                    viewModel.filterByCollection(collectionId)
                }

                CatalogScreen(
                    viewModel = viewModel,
                    onVolumeClick = { isbn ->
                        navController.navigate(Screen.Detail.createRoute(isbn))
                    }
                )
            }
            composable(Screen.Collections.route) {
                val viewModel: CollectionViewModel = hiltViewModel()
                CollectionScreen(
                    viewModel = viewModel,
                    onCollectionClick = { collectionId ->
                        navController.navigate(Screen.Catalog.createRoute(collectionId))
                    }
                )
            }
            composable(Screen.Scanner.route) {
                val viewModel: CameraOcrViewModel = hiltViewModel()
                CameraOcrScreen(
                    viewModel = viewModel,
                    onIsbnDetected = { isbn ->
                        navController.navigate(Screen.AddVolume.createRoute(isbn))
                    },
                    onVolumeClick = { isbn ->
                        navController.navigate(Screen.Detail.createRoute(isbn))
                    }
                )
            }
            composable(
                route = Screen.AddVolume.route,
                arguments = listOf(navArgument("isbn") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                val viewModel: AddVolumeViewModel = hiltViewModel()
                val isbn = backStackEntry.arguments?.getString("isbn")
                
                LaunchedEffect(isbn) {
                    isbn?.let { viewModel.setInitialIsbn(it) }
                }

                AddVolumeScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("isbn") { type = NavType.StringType })
            ) { backStackEntry ->
                val isbn = backStackEntry.arguments?.getString("isbn") ?: return@composable
                val viewModel: VolumeDetailViewModel = hiltViewModel()
                VolumeDetailScreen(
                    isbn = isbn,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onEditClick = { isbnToEdit ->
                        navController.navigate(Screen.AddVolume.createRoute(isbnToEdit))
                    }
                )
            }
            composable(Screen.Settings.route) {
                val viewModel: SettingsViewModel = hiltViewModel()
                SettingsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
