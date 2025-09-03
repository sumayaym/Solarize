package no.uio.ifi.in2000.team39.data.frost

import no.uio.ifi.in2000.team39.model.Observation
import no.uio.ifi.in2000.team39.model.Station
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FrostRepository (private val dataSource : FrostDataSource){

    // Henter den første station id, kan endres når vi skal implementere polygon search
    suspend fun getNearestStationId(lat:Double, lon : Double) : Station {
        return dataSource.getNearestStationIdResponse(lat, lon).data.first()
    }

    suspend fun getWeatherObservation(
        stationId: String,
        elements: List<String>,
        startDate: String,
        endDate: String
    ) : List<Observation> {
        return dataSource.getWeatherObservationsResponse(stationId, elements, startDate, endDate).data
    }

    fun testLogging() {
        CoroutineScope(Dispatchers.IO).launch {
            try {

                val station = getNearestStationId(59.0, 10.0)
                val observations = getWeatherObservation(
                    "SN18700",
                    elements = listOf("air_temperature, cloud_area_fraction"),
                    startDate = "2024-01-01",
                    endDate = "2024-01-01"
                )

                Log.d("FrostRepository", "Observations: $station")
                Log.d("FrostRepository", "Observations: $observations")
            } catch (e: Exception) {
                Log.e("FrostRepository", "Feil ved henting av værdata", e)
                Log.e("FrostRepository", "Feil ved henting av værdata", e)
            }
        }
    }

}

