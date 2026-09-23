package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Feed : Screen("feed", "Feed", Icons.Filled.Home, Icons.Outlined.Home)
    object Reels : Screen("reels", "Reels", Icons.Filled.Videocam, Icons.Outlined.Videocam)
    object CollabStudio : Screen("collab", "Studio", Icons.Filled.AddCircleOutline, Icons.Filled.AddCircleOutline)
    object VaultChat : Screen("vault", "Vault", Icons.Filled.Lock, Icons.Outlined.Lock)
    object Notifications : Screen("notifications", "Alerts", Icons.Filled.Notifications, Icons.Outlined.Notifications)
    object Explore : Screen("explore", "Explore", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)
    object Admin : Screen("admin", "Admin", Icons.Filled.AdminPanelSettings, Icons.Outlined.AdminPanelSettings)

    companion object {
        val bottomNavItems = listOf(Feed, Reels, CollabStudio, VaultChat, Profile)
    }
}
