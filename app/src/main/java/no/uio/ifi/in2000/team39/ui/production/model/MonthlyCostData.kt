package no.uio.ifi.in2000.team39.ui.production.model

interface MonthlyData {
    val month: Int
    val energyProduced: Double
    val costEquivalent: Double?
    val avgPrice: Double?
}

data class MonthlyFrostData(
    override val month: Int,
    override val energyProduced: Double,
    override val costEquivalent: Double? = null,
    override val avgPrice: Double? = null,
    val avgTemperature: Double? = null,
    val avgSnowCoverage: Double? = null,
    val avgCloudCoverage: Double? = null
) : MonthlyData

data class MonthlyPvgisData(
    override val month: Int,
    override val energyProduced: Double,
    override val costEquivalent: Double,
    override val avgPrice: Double? = null
) : MonthlyData