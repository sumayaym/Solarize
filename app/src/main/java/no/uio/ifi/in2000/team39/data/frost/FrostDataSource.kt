package no.uio.ifi.in2000.team39.data.frost

import android.content.ContentValues.TAG
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.request
import io.ktor.http.isSuccess
import no.uio.ifi.in2000.team39.model.frost.FrostStationResponse
import no.uio.ifi.in2000.team39.model.frost.FrostWeatherResponse
import javax.inject.Inject
import javax.inject.Named


class FrostDataSource @Inject constructor(
    @Named("FrostHttpClient") private val ktorHttpClient: HttpClient
) {

    suspend fun getNearestStationIdResponse(
        lat: Double,
        lon: Double
    ): FrostStationResponse {
        return ktorHttpClient.get("sources/v0.jsonld?") {
            parameter("types", "SensorSystem")
            parameter("geometry", "nearest(POINT($lon $lat))")
        }.body()
    }

    suspend fun getWeatherObservationsResponse(
        stationId: String,
        elements: List<String>,
        startDate: String,
        endDate: String
    ): FrostWeatherResponse? {
        val relativePath = "observations/v0.jsonld"

        try {
            val response = ktorHttpClient.get(relativePath) {
                parameter("sources", stationId)
                parameter("elements", elements.joinToString(","))
                parameter("referencetime", "$startDate/$endDate")
            }

            if (response.status.isSuccess()) {
                return response.body()
            } else {
                Log.w(
                    TAG,
                    "FROST API returnerte feil (${response.status}): ${response.request.url}"
                )
                return null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Feil ved henting fra FROST API (${relativePath}): ${e.message}")
            return null
        }
    }
}

