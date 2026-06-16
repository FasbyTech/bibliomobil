package com.fasby.bibliomobil.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Catalog : Screen("catalog?collectionId={collectionId}", "Catálogo", Icons.Default.LibraryBooks) {
        fun createRoute(collectionId: String?) = if (collectionId != null) "catalog?collectionId=$collectionId" else "catalog"
    }
    data object Scanner : Screen("scanner", "Escáner", Icons.Default.Camera)
    data object Collections : Screen("collections", "Colecciones", Icons.Default.LibraryBooks)
    data object AddVolume : Screen("add_volume?isbn={isbn}", "Añadir", Icons.Default.Add) {
        fun createRoute(isbn: String?) = if (isbn != null) "add_volume?isbn=$isbn" else "add_volume"
    }
    data object Detail : Screen("detail/{isbn}", "Detalle", Icons.Default.LibraryBooks) {
        fun createRoute(isbn: String) = "detail/$isbn"
    }
}
