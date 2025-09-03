package no.uio.ifi.in2000.team39

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team39.data.frost.FrostDataSource
import no.uio.ifi.in2000.team39.data.frost.FrostRepository
import no.uio.ifi.in2000.team39.model.frost.*
import org.junit.Assert.assertEquals
import org.junit.Test

class FrostRepositoryTest {

    private val mockDataSource: FrostDataSource = mockk()
    private val repository = FrostRepository(mockDataSource)

    @Test
    fun getNearestStationId_shouldReturnFirstStationId() = runBlocking {
        // Arrange
        val expectedStation = Station(id = "SN18700", name = "OSLO - BLINDERN", geometry = Geometry(listOf(10.0, 60.0)))
        val mockResponse = FrostStationResponse(data = listOf(expectedStation), totalItemCount = 1)
        coEvery { mockDataSource.getNearestStationIdResponse(any(), any()) } returns mockResponse

        // Act
        val actualStation = repository.fetchNearestStation(60.0, 10.0)

        // Assert
        assertEquals(expectedStation, actualStation)
    }

    @Test
    fun getWeatherObservation_shouldReturnObservations() = runBlocking {
        // Arrange
        val expectedObservations = listOf(
            Observation(sourceId = "test", referenceTime = "2025-05-06T10:00:00.000Z", observations = listOf(Element.AirTemperature(value = 15.5))),
            Observation(sourceId = "test", referenceTime = "2025-05-06T11:00:00.000Z", observations = listOf(Element.SnowCoverage(value = 0.0)))
        )
        val mockResponse = FrostWeatherResponse(data = expectedObservations)
        coEvery { mockDataSource.getWeatherObservationsResponse(any(), any(), any(), any()) } returns mockResponse

        // Act
        val actualObservations = repository.fetchWeatherObservations("test_station", listOf("temp", "snow"), "2025-05-06", "2025-05-07")

        // Assert
        assertEquals(expectedObservations, actualObservations)
    }

    @Test
    fun getMonthlyWeatherData_shouldAggregateAverages() = runBlocking {
        // Arrange
        val mockResponse = FrostWeatherResponse(
            data = listOf(
                Observation(
                    referenceTime = "2025-01-15T12:00:00.000Z",
                    observations = listOf(
                        Element.AirTemperature(value = -2.0),
                        Element.SnowCoverage(value = 5.0),
                        Element.CloudAreaFraction(value = 70.0)
                    ),
                    sourceId = "---"
                ),
                Observation(
                    referenceTime = "2025-01-20T12:00:00.000Z",
                    observations = listOf(
                        Element.AirTemperature(value = 0.0),
                        Element.SnowCoverage(value = 10.0),
                        Element.CloudAreaFraction(value = 80.0)
                    ),
                    sourceId = "---"
                )
            )
        )
        coEvery { mockDataSource.getWeatherObservationsResponse(any(), any(), any(), any()) } returns mockResponse

        val expectedMonthlyData = mutableMapOf(
            "air_temperature" to mutableListOf(-1.0, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN),
            "snow_coverage_type" to mutableListOf(7.5, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN),
            "cloud_area_fraction" to mutableListOf(75.0, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN)
        )

        // Act
        val actualMonthlyData = repository.calculateMonthlyWeatherAverages("test_station", "2024-01-01", "2025-01-01")

        // Assert
        assertEquals(expectedMonthlyData["air_temperature"]?.get(0), actualMonthlyData["air_temperature"]?.get(0))
        assertEquals(expectedMonthlyData["snow_coverage_type"]?.get(0), actualMonthlyData["snow_coverage_type"]?.get(0))
        assertEquals(expectedMonthlyData["cloud_area_fraction"]?.get(0), actualMonthlyData["cloud_area_fraction"]?.get(0))
        for (i in 1..11) {
            assertEquals(true, actualMonthlyData["air_temperature"]?.get(i)?.isNaN() ?: false)
            assertEquals(true, actualMonthlyData["snow_coverage_type"]?.get(i)?.isNaN() ?: false)
            assertEquals(true, actualMonthlyData["cloud_area_fraction"]?.get(i)?.isNaN() ?: false)
        }
    }

    @Test
    fun getMonthlySolarData_shouldCalculateProduction() = runBlocking {
        // Arrange
        coEvery { mockDataSource.getWeatherObservationsResponse(any(), any(), any(), any()) } returns FrostWeatherResponse(
            data = listOf(
                Observation(sourceId = "test", referenceTime = "2025-01-15T12:00:00.000Z", observations = listOf(Element.AirTemperature(value = 5.0), Element.SnowCoverage(value = 2.0), Element.CloudAreaFraction(value = 30.0)))
            )
        )
        val radiationList = List(12) { listOf(100.0) }

        val weatherData: Map<String, List<Double>> = mapOf(
            "air_temperature" to List(12) { if (it == 0) 5.0 else 6.0 },
            "snow_coverage_type" to List(12) { if (it == 0) 2.0 else 0.2 },
            "cloud_area_fraction" to List(12) { if (it == 0) 30.0 else 2.0 }
        )

        val expectedProduction = mutableListOf<Double>()
        val daysPerMonth = listOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val k1 = -0.017237
        val k2 = -0.040465
        val k3 = -0.004702
        val k4 = 0.000149
        val k5 = 0.000170
        val k6 = 0.000005

        for (i in 0..11) {
            val hourlyG = radiationList[i].getOrElse(0) { 0.0 }
            val solarKwh = hourlyG / 1000.0
            val averageTemperatureForMonth =
                weatherData["air_temperature"]?.getOrElse(i) { 6.0 } ?: 6.0
            val temperatureModule = averageTemperatureForMonth + 20.0
            val temperatureForFormula = temperatureModule - 25.0
            val lnSolarKwh = kotlin.math.ln(solarKwh.coerceAtLeast(0.001))
            val effrel = (1.0 +
                    k1 * lnSolarKwh +
                    k2 * lnSolarKwh * lnSolarKwh +
                    k3 * temperatureForFormula +
                    k4 * temperatureForFormula * lnSolarKwh +
                    k5 * temperatureForFormula * lnSolarKwh * lnSolarKwh +
                    k6 * temperatureForFormula * temperatureForFormula).coerceAtLeast(0.0)
            val averageSnowCoverageForMonth =
                weatherData["snow_coverage_type"]?.getOrElse(i) { 0.2 } ?: 0.2
            val snowCoverageImpact = (1 - (averageSnowCoverageForMonth / 4.0)).coerceIn(0.0, 1.0)
            val averageCloudCoverageForMonth =
                weatherData["cloud_area_fraction"]?.getOrElse(i) { 2.0 } ?: 2.0
            val cloudCoverageImpact =
                (1 - (averageCloudCoverageForMonth / 100.0)).coerceIn(0.0, 1.0)
            val losses = snowCoverageImpact * cloudCoverageImpact * (1 - 0.20)
            val monthlyEnergy = solarKwh * 10.0 * effrel * losses * daysPerMonth[i]
            expectedProduction.add(monthlyEnergy)
        }

        // Act
        val actualProduction = repository.estimateMonthlySolarEnergy(peakPower = 10.0, systemLoss = 0.20, radiationData = radiationList, weatherData = weatherData)

        // Assert
        for (i in 0..11) {
            assertEquals(expectedProduction[i], actualProduction[i], 0.001)
        }
    }
}