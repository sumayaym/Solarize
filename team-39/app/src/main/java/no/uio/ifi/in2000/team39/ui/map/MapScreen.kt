// View

package no.uio.ifi.in2000.team39.ui.map

import androidx.lifecycle.viewmodel.compose.viewModel // Viewmodel
import androidx.compose.foundation.layout.Box // Compose UI: Layout
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button // Compose UI: Material3
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.ui.Alignment // Compose UI: Diverse
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.runtime.Composable // Compose: State og livssyklus
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.AndroidView // AndroidView
import androidx.navigation.NavHostController // Navigation
import org.maplibre.android.maps.MapView // MapLibre
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.PropertyFactory.visibility
import org.maplibre.android.style.layers.RasterLayer
import org.maplibre.android.style.sources.RasterSource
import org.maplibre.android.style.sources.TileSet
import org.maplibre.android.style.layers.Property.*
import org.maplibre.android.camera.CameraPosition // MapLibre for å velge hvor kameraet starter

import org.maplibre.android.camera.CameraUpdateFactory

// Debugging
import android.util.Log // For LogCat

// Forslag til forbedring: del opp kode i mindre biter så det blir mer modulært

// Viser hele kartskjermen
@Composable
fun MapScreen(navController: NavHostController) { // NavController brukes ikke foreløpig
    val viewModel: MapViewModel = viewModel()
    val mapSettings = viewModel.mapSettings
    val isOsmVisible = viewModel.isOsmVisible

    //val context = LocalContext.current Kan fjernes?
    val keyboardController = LocalSoftwareKeyboardController.current
    var searchQuery by remember { mutableStateOf("") }

    var osmLayer by remember { mutableStateOf<RasterLayer?>(null) }
    var esriLayer by remember { mutableStateOf<RasterLayer?>(null) }
    val currentZoom = remember { mutableStateOf(0.0) }

    // Viser MapView i Compose gjennom AndroidView
    AndroidView(
        factory = { context ->
            MapView(context).apply {
                getMapAsync { map ->

                    // Kamera-posisjon under oppstart
                    val cameraPosition = CameraPosition.Builder()
                        .target(mapSettings.initialPosition)  // Startposisjon fra MapSettings
                        .zoom(mapSettings.initialZoom)  // Startzoom fra MapSettings
                        .build()
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                    // Zoom-nivå:
                    map.setMinZoomPreference(mapSettings.minZoom) // Maks/min zoom basert på innstillinger fra MapSettings
                    map.setMaxZoomPreference(mapSettings.maxZoom)

                    map.addOnCameraIdleListener { // Debugging: Zoom-nivå etter kamera beveger på seg
                        val zoom = map.cameraPosition.zoom
                        currentZoom.value = zoom
                        //Log.d("ZoomLevel", "Zoom-nivå: $zoom") // For LogCat
                    }

                    // Koordinater
                    map.addOnMapClickListener { point -> // Trykking på kart sender koordinater videre til MapViewModel.kt
                        val lat = point.latitude
                        val lng = point.longitude
                        viewModel.setTappedLocation(lat, lng)
                        true // Returnerer true for å indikere at klikket er håndtert
                    }

                    // Last inn tom stil og legg til kartlag
                    map.setStyle(Style.Builder().fromUri("asset://map/empty.json")) { style -> // Setter en tom stil (basert på en JSON-fil i assets) og legger til egne lag
                        val osmSource = RasterSource(
                            "osm-source",
                            TileSet("osm-tiles", "https://tile.openstreetmap.org/{z}/{x}/{y}.png"),
                            256
                        )
                        val osmLayerInstance = RasterLayer("osm-layer", "osm-source")

                        val esriSource = RasterSource(
                            "esri-source",
                            TileSet("esri-tiles", "https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}"),
                            256
                        )
                        val esriLayerInstance = RasterLayer("esri-layer", "esri-source")

                        style.addSource(osmSource)
                        style.addLayer(osmLayerInstance)
                        osmLayer = osmLayerInstance

                        style.addSource(esriSource)
                        style.addLayer(esriLayerInstance)
                        esriLayer = esriLayerInstance

                        // Start med OSM synlig og ESRI skjult
                        osmLayerInstance.setProperties(visibility(VISIBLE))
                        esriLayerInstance.setProperties(visibility(NONE))
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )

    // Oppdater synlighet av kartlag ved bytte
    LaunchedEffect(isOsmVisible) {
        osmLayer?.setProperties(visibility(if (isOsmVisible) VISIBLE else NONE))
        esriLayer?.setProperties(visibility(if (!isOsmVisible) VISIBLE else NONE))
    }

    // Overlay med søk, knapper og koordinater
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Column {
            // UI for adresse-søk – tekstfelt og søkeknapp
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Søk adresse") },
                modifier = Modifier.padding(16.dp)
            )

            Button(
                onClick = {
                    viewModel.searchAddress(searchQuery)
                    keyboardController?.hide()
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text("Søk")
            }

            // Knapp for å bytte mellom OSM og ESRI
            Button(
                onClick = { viewModel.toggleLayer() },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(if (isOsmVisible) "Bytt til ESRI" else "Bytt til OSM")
            }

            // Viser koordinatene til sist trykkede punkt på kartet
            viewModel.tappedLocation?.let {
                Text(
                    text = "Lat: ${it.latitude}, Lng: ${it.longitude}",
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = "Zoom-nivå: ${"%.2f".format(currentZoom.value)}",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

    // Sørger for at MapView blir ryddet opp riktig. Er den feil?
    DisposableEffect(Unit) {
        onDispose {
            // MapView-rydding hvis du bruker factory direkte
        }
    }
}
