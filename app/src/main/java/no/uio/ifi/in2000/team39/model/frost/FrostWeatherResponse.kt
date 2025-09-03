package no.uio.ifi.in2000.team39.model.frost

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FrostWeatherResponse(
    val data: List<Observation>
)

@Serializable
data class Observation(
    val sourceId: String,
    val referenceTime: String,
    val observations: List<Element>
)

@Polymorphic
@Serializable
sealed class Element {
    @Serializable
    @SerialName("mean(cloud_area_fraction P1M)")
    data class CloudAreaFraction(
        val value: Double,
        val unit: String? = null,
        val timeOffset: String? = null,
        val timeResolution: String? = null,
        val qualityCode: Int? = null
    ) : Element()

    @Serializable
    @SerialName("mean(air_temperature P1M)")
    data class AirTemperature(
        val value: Double,
        val unit: String? = null,
        val timeOffset: String? = null,
        val timeResolution: String? = null,
        val qualityCode: Int? = null,
        val level: Level? = null
    ) : Element()

    @Serializable
    @SerialName("mean(snow_coverage_type P1M)")
    data class SnowCoverage(
        val value: Double,
        val unit: String? = null,
        val timeOffset: String? = null,
        val timeResolution: String? = null,
        val qualityCode: Int? = null
    ) : Element()
}

@Serializable
data class Level(
    val levelType: String? = null,
    val unit: String? = null,
    val value: Double? = null
)