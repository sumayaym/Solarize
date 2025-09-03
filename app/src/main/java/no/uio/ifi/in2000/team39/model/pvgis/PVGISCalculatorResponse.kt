package no.uio.ifi.in2000.team39.model.pvgis

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PVGISCalculatorResponse(
    val outputs: CalculatorOutputs
)

@Serializable
data class CalculatorOutputs(
    val totals: TotalsWrapper,
    val monthly: MonthlyWrapper
)

@Serializable
data class TotalsWrapper(
    val fixed: YearlySolarData
)

@Serializable
data class MonthlyWrapper(
    val fixed: List<MonthlyData>
)

@Serializable
data class YearlySolarData(
    @SerialName("E_y")
    val yearlyEnergyProduction: Double,
    @SerialName("E_d")
    val averageDailyEnergyProduction: Double,
    @SerialName("E_m")
    val averageMonthlyEnergyProduction: Double,
)

@Serializable
data class MonthlyData(
    val month: Int,
    @SerialName("E_d")
    val dailyEnergyProduction: Double,
    @SerialName("E_m")
    val monthlyEnergyProduction: Double,
)

