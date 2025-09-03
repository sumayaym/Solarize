package no.uio.ifi.in2000.team39.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.team39.ui.SharedHomeViewModel
import no.uio.ifi.in2000.team39.ui.map.MapScreen
import no.uio.ifi.in2000.team39.ui.map.MapViewModel
import no.uio.ifi.in2000.team39.ui.production.ProdScreen
import no.uio.ifi.in2000.team39.ui.production.ProdscreenViewModel
import no.uio.ifi.in2000.team39.ui.production.ProfitabilityScreen
import no.uio.ifi.in2000.team39.ui.production.savinggraph.SavingsGraphDetailScreen
import no.uio.ifi.in2000.team39.ui.userprofile.UserProfileScreen

@Composable
fun AppNavigation() {
    val viewModel: NavigationViewModel = hiltViewModel()
    val navBarItems = createBottomNavbarItems()
    val navBarSelectedItemIndex = viewModel.navBarSelectedIndex.collectAsStateWithLifecycle().value
    val sharedHomeViewModel: SharedHomeViewModel = hiltViewModel()

    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination?.route

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar {
                navBarItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = currentDestination == item.title,
                        onClick = {
                            if (currentDestination != item.title) {
                                navController.navigate(item.title) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (index == navBarSelectedItemIndex)
                                    item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "production",
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable("mapScreen") { entry ->
                val mapViewModel: MapViewModel = hiltViewModel(entry)
                MapScreen(mapViewModel, sharedHomeViewModel)
            }

            composable("production") { entry ->
                val prodViewModel: ProdscreenViewModel = hiltViewModel(entry)
                ProdScreen(prodViewModel, sharedHomeViewModel, navController)
            }

            composable("savingGraphDetails") { entry ->
                val parentEntry = remember(entry) {
                    navController.getBackStackEntry("production")
                }
                val prodViewModel: ProdscreenViewModel = hiltViewModel(parentEntry)
                SavingsGraphDetailScreen(prodViewModel, navController)
            }

            composable("userProfile") { _ ->
                UserProfileScreen(sharedHomeViewModel)
            }

            composable("profitability") { entry ->
                val parentEntry = remember(entry) {
                    navController.getBackStackEntry("production")
                }
                val prodViewModel: ProdscreenViewModel = hiltViewModel(parentEntry)
                ProfitabilityScreen(prodViewModel, navController)
            }
        }
    }
}
