package no.uio.ifi.in2000.team39.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Place
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavBarItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

fun createBottomNavbarItems(): List<BottomNavBarItem> {
    return listOf(
        BottomNavBarItem(
            title = "production",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        BottomNavBarItem(
            title = "mapScreen",
            selectedIcon = Icons.Filled.Place,
            unselectedIcon = Icons.Outlined.Place
        ),
        BottomNavBarItem(
            title = "userProfile",
            selectedIcon = Icons.Filled.AccountCircle,
            unselectedIcon = Icons.Outlined.AccountCircle
        )
    )
}