package no.uio.ifi.in2000.team39.ui.map

import no.uio.ifi.in2000.team39.model.map.GeocodingResult
import no.uio.ifi.in2000.team39.model.map.SearchState
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng

data class MapUiState(
    val coordinates: LatLng? = null,
    val address: String? = null,
    val geocodingResult: GeocodingResult? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isOsmVisible: Boolean = true,
    val currentZoom: Double = 0.0,
    val fromSearch: Boolean = false,
    val cameraPosition: CameraPosition? = null,
    val searchState: SearchState = SearchState.Idle
)