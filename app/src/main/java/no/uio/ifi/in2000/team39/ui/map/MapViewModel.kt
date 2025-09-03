package no.uio.ifi.in2000.team39.ui.map

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team39.R
import no.uio.ifi.in2000.team39.data.map.MapLayers
import no.uio.ifi.in2000.team39.data.map.MapRepository
import no.uio.ifi.in2000.team39.model.map.GeocodingResult
import no.uio.ifi.in2000.team39.model.map.MapSettings
import no.uio.ifi.in2000.team39.model.map.SearchState
import no.uio.ifi.in2000.team39.ui.theme.OrangePrimary
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.Property.ICON_ANCHOR_BOTTOM
import org.maplibre.android.style.layers.Property.NONE
import org.maplibre.android.style.layers.Property.VISIBLE
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.layers.PropertyFactory.iconAllowOverlap
import org.maplibre.android.style.layers.PropertyFactory.iconAnchor
import org.maplibre.android.style.layers.PropertyFactory.iconColor
import org.maplibre.android.style.layers.PropertyFactory.iconIgnorePlacement
import org.maplibre.android.style.layers.PropertyFactory.iconImage
import org.maplibre.android.style.layers.PropertyFactory.visibility
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonSource
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val mapRepository: MapRepository
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()
    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions.asStateFlow()

    // Map Settings
    private val mapSettings = MapSettings()
    private var _mapLayers: MapLayers? = null
    private var mapLibreMap: MapLibreMap? = null

    // Map Initialization
    fun setMapLibreMap(map: MapLibreMap, context: Context) {
        _uiState.update { it.copy(isLoading = true) }
        mapLibreMap = map
        setupMap(context)
    }

    private fun setupMap(context: Context) {
        mapLibreMap?.apply {
            val initialCameraPosition = _uiState.value.cameraPosition ?: CameraPosition.Builder()
                .target(mapSettings.initialPosition)
                .zoom(mapSettings.initialZoom)
                .build()

            moveCamera(CameraUpdateFactory.newCameraPosition(initialCameraPosition))
            setMinZoomPreference(mapSettings.minZoom)
            setMaxZoomPreference(mapSettings.maxZoomOSM)

            addOnCameraIdleListener {
                _uiState.update { currentState ->
                    currentState.copy(
                        cameraPosition = cameraPosition,
                        currentZoom = cameraPosition.zoom
                    )
                }
                updateMarker()
            }

            setStyle(Style.Builder().fromUri("asset://map/empty.json")) { style ->
                initializeMap(style)
                initializeMarkerLayer(style, context)
                updateMarker()
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun initializeMap(style: Style) {
        _mapLayers = mapRepository.initializeMapStyle(style)
        updateLayerVisibility(uiState.value.isOsmVisible)
    }

    // Marker Layer Initialization
    private fun initializeMarkerLayer(style: Style, context: Context) {
        val markerSource = GeoJsonSource("marker-source")
        val markerLayer = SymbolLayer("marker-layer", "marker-source").withProperties(
            iconImage("marker-icon"),
            iconAllowOverlap(true),
            iconIgnorePlacement(true),
            iconAnchor(ICON_ANCHOR_BOTTOM),
            iconColor(OrangePrimary.toArgb()),
            PropertyFactory.iconSize(2.0f)
        )

        style.addSource(markerSource)
        style.addLayer(markerLayer)

        AppCompatResources.getDrawable(context, R.drawable.location)?.let { drawable ->
            val bitmap = drawable.toBitmap(width = 48, height = 48)
            style.addImage("marker-icon", bitmap)
        }
    }

    // Layer Visibility
    private fun updateLayerVisibility(isOsmVisible: Boolean) {
        _mapLayers?.let { layers ->
            layers.osmLayer.setProperties(visibility(if (isOsmVisible) VISIBLE else NONE))
            layers.esriLayer.setProperties(visibility(if (!isOsmVisible) VISIBLE else NONE))

            // Adjust zoom preferences based on active layer
            mapLibreMap?.apply {
                val maxZoom = if (isOsmVisible) mapSettings.maxZoomOSM else mapSettings.maxZoomESRI
                setMinZoomPreference(mapSettings.minZoom)
                setMaxZoomPreference(maxZoom)
            }
        }
    }

    // Layer Toggle with Feedback
    fun toggleLayer() {
        val isOsmVisible = !uiState.value.isOsmVisible
        val currentZoom = mapLibreMap?.cameraPosition?.zoom ?: 0.0

        if (!isOsmVisible && currentZoom > mapSettings.maxZoomESRI) {
            _snackbarMessage.value = "Zoom out to view ESRI imagery."
            mapLibreMap?.animateCamera(CameraUpdateFactory.zoomTo(mapSettings.maxZoomESRI))
        }

        _uiState.update { currentState ->
            currentState.copy(isOsmVisible = isOsmVisible).also {
                updateLayerVisibility(isOsmVisible)
            }
        }
    }

    // Clears the current snackbar message
    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    // Search Functions
    fun fetchSuggestions(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _suggestions.value = emptyList()
                return@launch
            }
            try {
                val results = mapRepository.getSuggestions(query)
                _suggestions.value = results
            } catch (e: Exception) {
                _suggestions.value = emptyList()
            }
        }
    }

    // Handles the address search and updates the UI state
    fun searchAddress(address: String) {
        _uiState.update { it.copy(isLoading = true, searchState = SearchState.Loading) }

        viewModelScope.launch {
            try {
                val result = mapRepository.searchAddress(address)
                if (result != null) {
                    handleSearchSuccess(result)
                } else {
                    _uiState.update {
                        it.copy(
                            searchState = SearchState.Error("Ingen adresser funnet"),
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        searchState = SearchState.Error(e.message ?: "Ukjent feil"),
                        isLoading = false
                    )
                }
            }
        }
    }

    // Handles successful address search results, updating map and marker
    private fun handleSearchSuccess(result: GeocodingResult) {
        val lat = result.lat.toDoubleOrNull() ?: 0.0
        val lon = result.lon.toDoubleOrNull() ?: 0.0
        val location = LatLng(lat, lon)

        // Build the address more reliably
        val displayName = result.displayName ?: buildString {
            result.address?.let { addr ->
                listOfNotNull(
                    addr.road,
                    addr.houseNumber,
                    addr.postcode,
                    addr.city ?: addr.town ?: addr.village,
                    addr.county
                ).joinTo(this, ", ")
            }
        }.ifBlank { "Ukjent sted" }

        // Update the UI state
        _uiState.update {
            it.copy(
                coordinates = location,
                address = displayName,
                geocodingResult = result,
                fromSearch = true,
                searchState = SearchState.Success(location),
                isLoading = false
            )
        }

        moveToLocation(location)
        updateMarker()
    }

    fun reverseGeocode(latitude: Double, longitude: Double) {
        _uiState.update { it.copy(isLoading = true, searchState = SearchState.Loading) }
        viewModelScope.launch {
            try {
                val result = mapRepository.reverseGeocode(latitude, longitude)
                if (result != null) {
                    handleReverseGeocodeSuccess(result, LatLng(latitude, longitude))
                } else {
                    _uiState.update {
                        it.copy(
                            searchState = SearchState.Error("Ingen adresse funnet"),
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        searchState = SearchState.Error(e.message ?: "Ukjent feil"),
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun handleReverseGeocodeSuccess(result: GeocodingResult, latLng: LatLng) {
        val displayName = result.displayName ?: buildString {
            result.address?.let { addr ->
                listOfNotNull(
                    addr.road,
                    addr.houseNumber,
                    addr.postcode,
                    addr.city ?: addr.town ?: addr.village,
                    addr.county
                ).joinTo(this, ", ")
            }
        }.ifBlank { "Ukjent sted" }

        _uiState.update {
            it.copy(
                coordinates = latLng,
                address = displayName,
                geocodingResult = result,
                fromSearch = false,
                searchState = SearchState.Success(latLng),
                isLoading = false
            )
        }
        moveToLocation(latLng)
        updateMarker()
    }


    private fun moveToLocation(latLng: LatLng) {
        val newCameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(17.0)
            .build()
        _uiState.update { it.copy(cameraPosition = newCameraPosition) }
        mapLibreMap?.animateCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition))
    }

    private fun updateMarker() {
        getMarkerGeoJson()?.let { geoJson ->
            mapLibreMap?.style?.getSourceAs<GeoJsonSource>("marker-source")?.setGeoJson(geoJson)
        }
    }

    // Generates the GeoJSON representation for the current marker position
    private fun getMarkerGeoJson(): String? {
        return uiState.value.coordinates?.let {
            """
        {
            "type": "Feature",
            "geometry": {
                "type": "Point",
                "coordinates": [${it.longitude}, ${it.latitude}]
            }
        }
        """.trimIndent()
        }
    }


}