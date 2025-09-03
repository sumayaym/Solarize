package no.uio.ifi.in2000.team39.model.hks

import kotlinx.serialization.Serializable

@Serializable
data class HKSPriceData(
    val nokPerKWh: Double,
    val eurPerKWh: Double = nokPerKWh / 10.0,
    val exr: Double? = null,
    val timeStart: String? = null,
    val timeEnd: String? = null
)

@Serializable
data class HKSParams(
    val year: String,
    val month: String = "01",
    val day: String = "01",
    val area: String
)
