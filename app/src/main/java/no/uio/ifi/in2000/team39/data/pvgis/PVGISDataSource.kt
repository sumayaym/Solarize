package no.uio.ifi.in2000.team39.data.pvgis


import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import no.uio.ifi.in2000.team39.model.pvgis.PVGISCalculatorResponse
import no.uio.ifi.in2000.team39.model.pvgis.RadiationResponse
import no.uio.ifi.in2000.team39.model.pvgis.SolarParams
import javax.inject.Inject


class PVGISDataSource @Inject constructor(
    private val client: HttpClient
) {

    suspend fun fetchSolarData(params: SolarParams): PVGISCalculatorResponse {

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

    suspend fun fetchMonthlyRadiationData(params: SolarParams): RadiationResponse {

        return client.get("https://re.jrc.ec.europa.eu/api/v5_3/DRcalc") {
            url {
                parameters.append("lat", params.lat.toString())
                parameters.append("lon", params.lon.toString())
                parameters.append("angle", params.angle.toString())
                parameters.append("aspect", params.aspect.toString())
                parameters.append("month", params.month.toString())
                parameters.append("global", 1.toString())
                parameters.append("outputformat", "json")
            }
        }.body()
    }


}