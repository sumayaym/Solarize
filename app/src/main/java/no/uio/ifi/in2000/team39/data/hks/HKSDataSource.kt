package no.uio.ifi.in2000.team39.data.hks

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import no.uio.ifi.in2000.team39.model.hks.HKSParams
import no.uio.ifi.in2000.team39.model.hks.HKSPriceData
import javax.inject.Inject

class HKSDataSource @Inject constructor(
    private val client: HttpClient
) {
    suspend fun fetchElectricityPrices(params: HKSParams): List<HKSPriceData> {
        val url =
            "https://www.hvakosterstrommen.no/api/v1/prices/${params.year}/${params.month}-${params.day}_${params.area}.json"
        return client.get(url).body()
    }
}
