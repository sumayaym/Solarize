package no.uio.ifi.in2000.team39

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team39.data.hks.HKSDataSource
import no.uio.ifi.in2000.team39.data.hks.HKSRepository
import no.uio.ifi.in2000.team39.model.hks.HKSParams
import no.uio.ifi.in2000.team39.model.hks.HKSPriceData
import org.junit.Before
import org.junit.Test

class HKSRepositoryTest {

    private val mockDataSource = mockk<HKSDataSource>()
    private val repository = HKSRepository(mockDataSource)

    @Before
    fun setUp() {
        mockkStatic("android.util.Log")
        every { android.util.Log.e(any(), any()) } returns 0
    }
    @Test
    fun testCalculateMonthlyAveragePrices() = runBlocking {
        // Arrange
        val params = HKSParams(year = "2025", month = "01", day = "10", area = "NO1")
        val mockPricesJan = listOf(HKSPriceData(nokPerKWh = 1.0), HKSPriceData(nokPerKWh = 1.2))
        val mockPricesFeb = listOf(HKSPriceData(nokPerKWh = 1.5), HKSPriceData(nokPerKWh = 1.7))
        val mockPricesMar = emptyList<HKSPriceData>()
        val mockPricesApr = listOf(HKSPriceData(nokPerKWh = 2.0))

        coEvery { mockDataSource.fetchElectricityPrices(HKSParams(year = "2025", month = "01", day = "10", area = "NO1")) } returns mockPricesJan
        coEvery { mockDataSource.fetchElectricityPrices(HKSParams(year = "2025", month = "02", day = "10", area = "NO1")) } returns mockPricesFeb
        coEvery { mockDataSource.fetchElectricityPrices(HKSParams(year = "2025", month = "03", day = "10", area = "NO1")) } returns mockPricesMar
        coEvery { mockDataSource.fetchElectricityPrices(HKSParams(year = "2025", month = "04", day = "10", area = "NO1")) } returns mockPricesApr

        val expectedAverages = listOf(1.1, 1.6, 1.0, 2.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)

        // Act
        val actualAverages = repository.calculateMonthlyAveragePrices(params)

        // Assert
        assertEquals(expectedAverages, actualAverages)

    }

}