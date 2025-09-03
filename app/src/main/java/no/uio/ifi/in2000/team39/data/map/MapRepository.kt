package no.uio.ifi.in2000.team39.data.map

import no.uio.ifi.in2000.team39.model.map.GeocodingResult
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.RasterLayer
import javax.inject.Inject

class MapRepository @Inject constructor(
    private val mapSearchDataSource: MapSearchDataSource,
    private val mapDataSource: MapDataSource
) {
    suspend fun searchAddress(address: String): GeocodingResult? {
        return mapSearchDataSource.geocodeAddress(address)
    }

    suspend fun reverseGeocode(latitude: Double, longitude: Double): GeocodingResult? {
        return mapSearchDataSource.reverseGeocode(latitude, longitude)
    }

    fun initializeMapStyle(style: Style): MapLayers {
        val (osmLayer, esriLayer) = mapDataSource.setupRasterLayers(style)
        return MapLayers(osmLayer, esriLayer)
    }

    suspend fun getSuggestions(query: String): List<String> {
        return mapSearchDataSource.getSearchSuggestions(query)
    }
}


data class MapLayers(
    val osmLayer: RasterLayer,
    val esriLayer: RasterLayer
)
