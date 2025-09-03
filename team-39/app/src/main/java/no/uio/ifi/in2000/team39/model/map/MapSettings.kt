// View

package no.uio.ifi.in2000.team39.model.map
import org.maplibre.android.geometry.LatLng


data class MapSettings(
    val minZoom: Double = 3.0,
    val maxZoom: Double = 17.0, // Kan ikke zoome inn mer enn 17 pga. ESRI

    val initialZoom: Double = 4.0,                  // Start-zoom når kartet lastes
    val initialPosition: LatLng = LatLng(64.5, 11.0) // Startposisjon i kartet (midt i Norge)
)

/*
*
*
*
*
*
*
* */