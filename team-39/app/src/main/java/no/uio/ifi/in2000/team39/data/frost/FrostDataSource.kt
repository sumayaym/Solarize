package no.uio.ifi.in2000.team39.data.frost

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json

import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.team39.model.FrostStationResponse
import no.uio.ifi.in2000.team39.model.FrostWeatherResponse


const val baseUrl = "https://in2000-proxy.ifi.uio.no/alpacaapi/v2/alpacaparties"

class FrostDataSource{

    private val frostClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true
                classDiscriminator = "elementId"
            })
        }
        defaultRequest {
            url("https://frost.met.no")
            header(HttpHeaders.Accept, "application/json")
            basicAuth("c0a557b4-a397-4285-925a-1e035690a53a", "")
        }
    }

    suspend fun getNearestStationIdResponse(
        lat : Double,
        lon : Double
    ) : FrostStationResponse {
        return frostClient.get("sources/v0.jsonld?") {
            parameter("types", "SensorSystem")
            parameter("geometry", "nearest(POINT($lon $lat))")
        }.body()
    }

    suspend fun getWeatherObservationsResponse(
        stationId: String,
        elements: List<String>,
        startDate: String,
        endDate: String
    ): FrostWeatherResponse {
        return frostClient.get("observations/v0.jsonld") {
            parameter("sources", stationId)
            parameter("elements", elements.joinToString(","))
            parameter("referencetime", "$startDate/$endDate")
        }.body()
    }
/// FIKS KANKSJE LEVEL TIL DEFAULT
}
