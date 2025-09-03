package no.uio.ifi.in2000.team39.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import no.uio.ifi.in2000.team39.ui.userprofile.UserProfileScreen
import no.uio.ifi.in2000.team39.ui.map.MapScreen
import no.uio.ifi.in2000.team39.ui.components.BottomNavigationBar

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "mapScreen",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("mapScreen") { MapScreen(navController) }
            composable("userProfile") { UserProfileScreen() }
        }
    }
}
