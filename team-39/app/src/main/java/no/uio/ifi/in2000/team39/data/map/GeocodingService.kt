package no.uio.ifi.in2000.team39.data.map

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import no.uio.ifi.in2000.team39.model.map.GeocodingResult
import kotlinx.serialization.json.Json

// Forslag til forbedring: Feilhåndtering
// Søker man på noe ugyldig, får man null. Kan f.eks. sende feilmelding til Viewmodel

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true // Ignorerer ukjente JSON-felter fra API-et
        })
    }
}

suspend fun geocodeAddress(address: String): Pair<Double, Double>? {
    val response = client.get("https://nominatim.openstreetmap.org/search") {
        parameter("q", address) // Søkestreng
        parameter("format", "json") // Returner JSON
        parameter("limit", 1) // Kun første resultat
        header("User-Agent", "team39-mapapp") // Påkrevd av Nominatim (requests blokkeres uten dette)
    }

    val result: List<GeocodingResult> = response.body()


    return result.firstOrNull()?.let {
        it.lat.toDouble() to it.lon.toDouble()
    }
}