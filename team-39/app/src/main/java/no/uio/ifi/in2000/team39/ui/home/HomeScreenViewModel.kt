package no.uio.ifi.in2000.team39.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team39.data.frost.FrostRepository

class HomeViewModel(private val frostRepository: FrostRepository) : ViewModel(){


    fun fetchNearestStation(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val stationId = frostRepository.getNearestStationId(lat, lon)
                //_uiState.value = UIState.Success(parties)
            } catch (e: Exception) {
               // _uiState.value = UIState.Error(e.message ?: "Error")
            }
        }
    }

    fun fetchWeatherObservations(stationId: String) {
        viewModelScope.launch {
            try {

                val observations = frostRepository.getWeatherObservation(stationId, listOf("air_temperature", "cloud_area_fraction"), "2024-01-01", "2024-01-01")
                //_uiState.value = UIState.Success(parties)
            } catch (e: Exception) {
                // _uiState.value = UIState.Error(e.message ?: "Error")
            }
        }
    }
}
