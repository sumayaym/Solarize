package no.uio.ifi.in2000.team39.data.pvgis


import no.uio.ifi.in2000.team39.data.pvgis.PVGISDataSource
import no.uio.ifi.in2000.team39.data.pvgis.PVGISResponse
import no.uio.ifi.in2000.team39.data.pvgis.SolarParams


// repo for å  hente og cache solproduksjonsdata fra PVGIS i minne
class PVGISRepository(
    private val dataSource: PVGISDataSource = PVGISDataSource()
) {
    private var cachedResponse: PVGISResponse? = null

    suspend fun getSolarData(params: SolarParams): PVGISResponse {
        return cachedResponse ?: run {
            val response = dataSource.fetchSolarData(params)
            cachedResponse = response
            response
        }
    }
}
