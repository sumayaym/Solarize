// ViewModel

package no.uio.ifi.in2000.team39.ui.map
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team39.data.map.geocodeAddress
import no.uio.ifi.in2000.team39.model.map.MapSettings
import org.maplibre.android.geometry.LatLng


class MapViewModel : ViewModel() {
    val mapSettings = MapSettings() // Henter kartinnstillinger fra dataklasse

    var isOsmVisible by mutableStateOf(true) // Tilstand: Skal OSM være synlig? (true = OSM, false = ESRI)
        private set

    fun toggleLayer() { // Funksjon for å bytte mellom OSM og ESRI
        isOsmVisible = !isOsmVisible
    }

    var tappedLocation by mutableStateOf<LatLng?>(null) // Koordinatene der brukeren trykket på kartet, null hvis ingen trykk enda
        private set

    fun setTappedLocation(lat: Double, lng: Double) { // Setter ny lokasjon basert på lat/lng – kalles fra MapScreen når bruker trykker på kart
        tappedLocation = LatLng(lat, lng)
    }

    fun searchAddress(address: String) { // Søker etter en adresse og oppdaterer koordinatene hvis funnet
        viewModelScope.launch {
            // Kall på GeocodingService for å hente lat/lng
            val result = geocodeAddress(address)

            // Hvis vi får et resultat, sett koordinatene i ViewModel
            result?.let { (lat, lon) ->
                setTappedLocation(lat, lon)
            }
        }
    }
}