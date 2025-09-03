package no.uio.ifi.in2000.team39.model.map

import org.maplibre.android.geometry.LatLng

data class MapSettings(
    val minZoom: Double = 3.0,
    val maxZoomOSM: Double = 20.0,
    val maxZoomESRI: Double = 17.0,
    val initialZoom: Double = 4.0,
    val initialPosition: LatLng = LatLng(64.5, 11.0)
)