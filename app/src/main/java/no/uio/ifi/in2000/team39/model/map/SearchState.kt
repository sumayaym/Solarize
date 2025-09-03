package no.uio.ifi.in2000.team39.model.map

import org.maplibre.android.geometry.LatLng

sealed interface SearchState {
    data object Idle : SearchState
    data object Loading : SearchState
    data class Success(val coordinates: LatLng) : SearchState
    data class Error(val message: String) : SearchState
}
