package no.uio.ifi.in2000.team39.ui.map.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import no.uio.ifi.in2000.team39.ui.map.MapViewModel
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView

// This is the map view component

@Composable
fun MapViewComponent(
    mapViewModel: MapViewModel,
    onMapClick: () -> Unit,
    onMapLongClick: (LatLng) -> Unit
) {
    val context = LocalContext.current

    val mapView = remember {
        MapView(context).apply {
            onCreate(null)
            getMapAsync { map ->
                mapViewModel.setMapLibreMap(map, context)
                map.addOnMapClickListener { _ ->
                    onMapClick()
                    true
                }
                map.addOnMapLongClickListener { latLng ->
                    onMapLongClick(latLng)
                    true
                }
            }
        }
    }

    AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())
}
