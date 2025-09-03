package no.uio.ifi.in2000.team39

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import no.uio.ifi.in2000.team39.ui.theme.Team39Theme
import org.maplibre.android.MapLibre
import no.uio.ifi.in2000.team39.navigation.AppNavigation
import no.uio.ifi.in2000.team39.ui.solar.SolarScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MapLibre.getInstance(this)
        setContent {
            Team39Theme {
                AppNavigation()
            }
        }
    }
}



