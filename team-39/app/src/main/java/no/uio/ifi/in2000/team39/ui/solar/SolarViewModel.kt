package no.uio.ifi.in2000.team39.ui.solar

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team39.data.pvgis.PVGISRepository
import no.uio.ifi.in2000.team39.data.pvgis.SolarParams

// UI-staten som observeres av UI-laget. Brukes for å rapportere endringer i tilstand (f.eks. ved API-kall), og oppdatere skjermen reaktivt.
data class SolarUiState(
    val annualProduction: Double? = null, // E_y: Årlig estimert produksjon
    val monthlyProduction: List<Double>? = null, // Liste over E_m per måned (kWh)
    val isLoading: Boolean = false,
    val error: String? = null
)

class SolarViewModel(
    private val repository: PVGISRepository = PVGISRepository()
) : ViewModel() {

    //  opptretter state varibel som kan observes
    var uiState by mutableStateOf(SolarUiState())
        private set

    fun loadProduction() {
        val params = SolarParams(lat = 59.91, lon = 10.75) // Midlertidig hardkodet Oslo

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            Log.d("PVGIS", "Starter API-kall…")

            try {
                val result = repository.getSolarData(params)

                val ey = result.outputs.totals.fixed.E_y
                val monthly = result.outputs.monthly.fixed.map { it.E_m }

                uiState = uiState.copy(
                    annualProduction = ey,
                    monthlyProduction = monthly,
                    isLoading = false
                )

                Log.d("PVGIS", " E_y: $ey kWh")
                monthly.forEachIndexed { index, value ->
                    Log.d("PVGIS", " Måned ${index + 1}: $value kWh")
                }

            } catch (e: Exception) {
                Log.e("PVGIS", " Feil: ${e.message}")
                uiState = uiState.copy(error = e.message ?: "Ukjent feil", isLoading = false)
            }
        }
    }
}
