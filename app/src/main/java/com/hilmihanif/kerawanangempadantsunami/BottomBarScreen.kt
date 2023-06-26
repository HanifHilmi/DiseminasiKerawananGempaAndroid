package com.hilmihanif.kerawanangempadantsunami

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Profil : BottomBarScreen(
        route = "Profil",
        title = "Profil",
        icon = Icons.Default.Person
    )

    object Beranda : BottomBarScreen(
        route = "Beranda",
        title = "Beranda",
        icon = Icons.Default.Home
    )

    object Kerawanan : BottomBarScreen(
        route = "Kerawanan",
        title = "Kerawanan",
        icon = Icons.Default.Info
    )
}