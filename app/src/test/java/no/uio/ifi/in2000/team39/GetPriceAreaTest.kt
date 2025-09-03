package no.uio.ifi.in2000.team39

import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import no.uio.ifi.in2000.team39.model.hks.PowerPriceArea
import no.uio.ifi.in2000.team39.ui.SharedHomeViewModel
import org.junit.Test

class GetPriceAreaTest {
    private val viewModel = SharedHomeViewModel(mockk())

    @Test
    fun testGetPriceAreaN01() {
        // Arrange
        val address = "Storgata 1, Oslo, Oslo"
        val expectedPriceArea = PowerPriceArea.NO1.name

        // Act
        val actualPriceArea = viewModel.getPriceArea(address)

        // Assert
        assertEquals(expectedPriceArea, actualPriceArea)
    }

    @Test
    fun testGetPriceAreaN02() {
        // Arrange
        val address = "Strandveien 5, Kristiansand, Agder"
        val expectedPriceArea = PowerPriceArea.NO2.name

        // Act
        val actualPriceArea = viewModel.getPriceArea(address)

        // Assert
        assertEquals(expectedPriceArea, actualPriceArea)
    }

    @Test
    fun testGetPriceAreaN03() {
        // Arrange
        val address = "Holtermanns veg 1, Trondheim, Trøndelag"
        val expectedPriceArea = PowerPriceArea.NO3.name

        // Act
        val actualPriceArea = viewModel.getPriceArea(address)

        // Assert
        assertEquals(expectedPriceArea, actualPriceArea)
    }

    @Test
    fun testGetPriceAreaN04() {
        // Arrange
        val address = "Sjøgata 10, Tromsø, Troms"
        val expectedPriceArea = PowerPriceArea.NO4.name

        // Act
        val actualPriceArea = viewModel.getPriceArea(address)

        // Assert
        assertEquals(expectedPriceArea, actualPriceArea)
    }

    @Test
    fun testGetPriceAreaUnknownCity() {
        // Arrange
        val address = "Parkveien 20, by"
        val expectedPriceArea = PowerPriceArea.NO1.name // Forventer default NO1

        // Act
        val actualPriceArea = viewModel.getPriceArea(address)

        // Assert
        assertEquals(expectedPriceArea, actualPriceArea)
    }

    @Test
    fun testGetPriceAreaEmptyCity() {
        // Arrange
        val address = "Hovedveien 3," // Mangler fylke
        val expectedPriceArea = PowerPriceArea.NO1.name // Forventer default NO1

        // Act
        val actualPriceArea = viewModel.getPriceArea(address)

        // Assert
        assertEquals(expectedPriceArea, actualPriceArea)
    }

    @Test
    fun testGetPriceAreaOnlyComma() {
        // Arrange
        val address = ","
        val expectedPriceArea = PowerPriceArea.NO1.name // Forventer default NO1

        // Act
        val actualPriceArea = viewModel.getPriceArea(address)

        // Assert
        assertEquals(expectedPriceArea, actualPriceArea)
    }

    @Test
    fun testGetPriceAreaEmptyAddress() {
        // Arrange
        val address = ""
        val expectedPriceArea = PowerPriceArea.NO1.name // Forventer default NO1

        // Act
        val actualPriceArea = viewModel.getPriceArea(address)

        // Assert
        assertEquals(expectedPriceArea, actualPriceArea)
    }
}