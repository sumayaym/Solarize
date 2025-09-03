package no.uio.ifi.in2000.team39.ui.solar



import android.util.Log
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

@Composable
fun SolarScreen() {
    val viewModel = remember { SolarViewModel() }

    LaunchedEffect(Unit) {
        viewModel.loadProduction()
        Log.d("SolarScreen", " PVGIS-kall utført")
    }

    Text("🔋 Tester PVGIS-kall – sjekk Logcat!")
}
