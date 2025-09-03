package no.uio.ifi.in2000.team39.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState


@Composable
fun BottomNavigationBar(navController: NavHostController) { // skal være felles i appen, ikon endrer farge aktiv/inaktiv

    val backStackEntry = navController.currentBackStackEntryAsState() // Får nåværende rute dynamisk
    val currentRoute = backStackEntry.value?.destination?.route

    val activeColor = Color(0xFF888C89) // grå når aktiv
    val inactiveColor = Color.Black // Svart når inaktiv

    BottomAppBar(
        containerColor = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = {
                    if (currentRoute != "mapScreen") {
                        navController.navigate("mapScreen")
                    }
                }
            ) {
                Icon(
                    Icons.Default.Place,
                    contentDescription = "Kart",
                    tint = if (currentRoute == "mapScreen") activeColor else inactiveColor
                )
            }

            IconButton(onClick = { }) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Lønnsomhet",
                    tint = if (currentRoute == "profitScreen") activeColor else inactiveColor  // Bytt "profitScreen" til riktig rute
                )
            }

            IconButton(
                onClick = {
                    if (currentRoute != "userProfile") {
                        navController.navigate("userProfile")
                    }
                }
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Min side",
                    tint = if (currentRoute == "userProfile") activeColor else inactiveColor
                )
            }
        }
    }
}
