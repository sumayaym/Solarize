package no.uio.ifi.in2000.team39.data.map

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import no.uio.ifi.in2000.team39.model.map.GeocodingResult
import no.uio.ifi.in2000.team39.model.map.PhotonResponse
import javax.inject.Inject


class MapSearchDataSource @Inject constructor(private val client: HttpClient) {

    suspend fun geocodeAddress(address: String): GeocodingResult? {
        return try {
            val response = client.get("https://nominatim.openstreetmap.org/search") {
                parameter("q", address)
                parameter("format", "json")
                parameter("limit", 1)
                parameter("addressdetails", 1)
                header("User-Agent", "team39-mapapp")
            }
            response.body<List<GeocodingResult>>().firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun reverseGeocode(latitude: Double, longitude: Double): GeocodingResult? {
        return try {
            val response = client.get("https://nominatim.openstreetmap.org/reverse") {
                parameter("lat", latitude)
                parameter("lon", longitude)
                parameter("format", "jsonv2")
                parameter("addressdetails", 1)
                header("User-Agent", "team39-mapapp")
            }
            response.body<GeocodingResult>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getSearchSuggestions(query: String): List<String> {
        return try {
            val response = client.get("https://photon.komoot.io/api/") {
                parameter("q", query)
                parameter("limit", 10)
                parameter("bbox", "4.0,57.9,31.0,71.5")
            }

            val results = response.body<PhotonResponse>()

            results.features
                .filter { feature ->
                    val country = feature.properties.country?.lowercase()
                    country == "norway" || country == "norge" || country == "no"
                }
                .map { feature ->
                    listOfNotNull(
                        feature.properties.name,
                        feature.properties.street,
                        feature.properties.housenumber,
                        feature.properties.postcode,
                        feature.properties.city,
                    ).joinToString(", ")
                }
                .distinct()
                .sorted()

        } catch (e: Exception) {
            emptyList()
        }
    }
}