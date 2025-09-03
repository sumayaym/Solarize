package no.uio.ifi.in2000.team39

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team39.data.pvgis.PVGISDataSource
import no.uio.ifi.in2000.team39.data.pvgis.PVGISRepository
import no.uio.ifi.in2000.team39.model.pvgis.*
import org.junit.Assert.assertEquals
import org.junit.Test

class PVGISRepositoryTest {

    private val mockDataSource: PVGISDataSource = mockk()
    private val repository = PVGISRepository(mockDataSource)
    private val testParams = SolarParams(lat = 60.0, lon = 10.0)

    @Test
    fun getSolarData_shouldReturnCorrectPVGISResponse() = runBlocking {
        // Arrange
        val expectedResponse = PVGISCalculatorResponse(
            CalculatorOutputs(
                TotalsWrapper(YearlySolarData(1000.0, 3.0, 100.0)),
                MonthlyWrapper(listOf(MonthlyData(1, 0.1, 3.0)))
            )
        )
        coEvery { mockDataSource.fetchSolarData(testParams) } returns expectedResponse

        // Act
        val actualResponse = repository.fetchSolarData(testParams)

        // Assert
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun getMonthlyRadiationData_shouldProcessDRCalcHourlyData() = runBlocking {
        // Arrange
        val mockRadiationResponse = RadiationResponse(
            RadiationOutputs(
                listOf(
                    HourlyData(1, "", 5.0),
                    HourlyData(2, "", 5.0 ),
                    HourlyData(3, "", 5.3),
                    HourlyData(4, "", 3.2 ),
                    HourlyData(4, "", 3.2),
                    HourlyData(5, "", 1.0)
                )
            )
        )
        coEvery { mockDataSource.fetchMonthlyRadiationData(testParams) } returns mockRadiationResponse
        val expectedRadiation = listOf(
            (100.0 + 150.0) / 2.0 / 1000.0, // Januar
            (300.0 + 150.0) / 2.0 / 1000.0, // Februar
            (250.0 + 250.0) / 2.0 / 1000.0, // Mars
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
        )

        // Act
        val monthlyRadiation = repository.fetchMonthlyRadiation(testParams)

        // Assert
        assertEquals(expectedRadiation, monthlyRadiation)
    }

    @Test
    fun getMonthlyRadiationData_withEmptyDRCalcHourlyList_shouldReturnListOfZeroes() = runBlocking {
        // Arrange
        coEvery { mockDataSource.fetchMonthlyRadiationData(testParams) } returns
                RadiationResponse(RadiationOutputs(emptyList()))
        val expectedRadiation = List(12) { 0.0 }

        // Act
        val monthlyRadiation = repository.fetchMonthlyRadiation(testParams)

        // Assert
        assertEquals(expectedRadiation, monthlyRadiation)
    }

}