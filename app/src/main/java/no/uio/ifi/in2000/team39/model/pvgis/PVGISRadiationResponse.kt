package no.uio.ifi.in2000.team39.model.pvgis

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RadiationResponse(
    val outputs: RadiationOutputs
)

@Serializable
data class RadiationOutputs(
    @SerialName("daily_profile")
    val hourly: List<HourlyData>
)

@Serializable
data class HourlyData(
    val month: Int,
    val time: String,
    @SerialName("G(i)")
    val irradianceOnTiltedSurface: Double
)
