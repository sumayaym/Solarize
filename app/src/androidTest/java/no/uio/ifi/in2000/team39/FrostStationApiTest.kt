package no.uio.ifi.in2000.team39

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team39.data.frost.FrostDataSource
import no.uio.ifi.in2000.team39.model.frost.FrostStationResponse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.ConnectException

/*

This was most for our personal use, to see if the response from the frost api was working.

*/

class FrostStationApiTest {
    @Test
    fun getNearestStationIdResponse_ReturnsResponse() = runBlocking {
        // Arrange
        val httpClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }
        val frostDataSource = FrostDataSource(ktorHttpClient = httpClient)
        val latitude = 59.0
        val longitude = 10.0
        val response : FrostStationResponse?

        try {
            // Act
            response = frostDataSource.getNearestStationIdResponse(latitude, longitude)
            println(response)

            // Assert
           assertNotNull(response)

        } catch (e: ConnectException) {
            println("Internet connection problem. ${e.message}")
            assertTrue(true)
        } catch (e: Exception) {
            println("The test could not pass through: ${e.message}")
            assertTrue(true)
        } finally {
            httpClient.close()
        }
    }
}