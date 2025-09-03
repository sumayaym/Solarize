package no.uio.ifi.in2000.team39

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team39.data.hks.HKSDataSource
import no.uio.ifi.in2000.team39.model.hks.HKSParams
import org.junit.Test
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.team39.model.hks.HKSPriceData

class HKSDataSourceAPIMockTest {
    @Test
    fun testFetchPrices() = runBlocking {
        // Arrange
        val expectedUrl = "https://www.hvakosterstrommen.no/api/v1/prices/2025/05-07_NO1.json"
        val params = HKSParams(year = "2025", month = "05", day = "07", area = "NO1")
        var actualUrl: String? = null
        val mockResponseData = listOf(
            HKSPriceData(
                nokPerKWh = 1.0,
                eurPerKWh = 0.1,
                exr = 102.0,
                timeStart = "00:00",
                timeEnd = "01.00"
            )
        )

        val mockEngine = MockEngine { request: HttpRequestData ->
            actualUrl = request.url.toString()
            respond(
                content = Json.encodeToString(mockResponseData),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val mockClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json()
            }
        }
        val dataSource = HKSDataSource(mockClient)

        // Act
        val actualPrices = dataSource.fetchElectricityPrices(params)

        // Assert
        assertEquals(expectedUrl, actualUrl) // Sjekk for URL
        assertEquals(mockResponseData, actualPrices) // Sjekk for responsdata
    }
}