package no.uio.ifi.in2000.team39.data.pvgis


import kotlinx.serialization.json.Json
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*


class PVGISDataSource {

    private val client = HttpClient(CIO) { // opprette en felles httpklient i prosjektet senere
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }

    }

    suspend fun fetchSolarData(params: SolarParams): PVGISResponse {  // Gjør et API-kall til PVGIS med gitte parametere og returnerer parsede soldata

        return client.get("https://re.jrc.ec.europa.eu/api/v5_3/PVcalc") {
            url {
                parameters.append("lat", params.lat.toString())
                parameters.append("lon", params.lon.toString())
                parameters.append("peakpower", params.peakPower.toString())
                parameters.append("loss", params.loss.toString())
                parameters.append("angle", params.angle.toString())
                parameters.append("aspect", params.aspect.toString())
                parameters.append("pvtechchoice", params.pvtech)
                parameters.append("mountingplace", params.mounting)
                parameters.append("outputformat", "json")
            }
        }.body()
    }


}