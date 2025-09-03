package no.uio.ifi.in2000.team39.model

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate


// To find station id:
@Serializable
data class FrostStationResponse(
    @SerialName("@type") val type: String,
    val data: List<Station>,
    val totalItemCount : Int
)

@Serializable
data class Station(
    val id: String,
    val name: String,
    val geometry: Geometry,
    val distance: Double,
    val validFrom: String,
)

// Kan sløyfes
@Serializable
data class Geometry(
    @SerialName("@type") val type: String,
    val coordinates: List<Double>
)

// To get weather data:

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
    @SerialName("cloud_area_fraction")
    data class CloudAreaFraction(
        val value: Int,
        val unit: String,
        val timeOffset: String,
        val timeResolution: String,
        val qualityCode: Int
    ) : Element()

    @Serializable
    @SerialName("air_temperature")
    data class AirTemperature(
        val value: Double,
        val unit: String,
        val timeOffset: String,
        val timeResolution: String,
        val qualityCode: Int,
        val level: Level
    ) : Element()

    @Serializable
    @SerialName("precipitation_amount")
    data class SnowDepth(
        val value: Double,
        val unit: String,
        val timeOffset: String,
        val timeResolution: String,
        val qualityCode: Int
    ) : Element()
}

@Serializable
data class Level(
    val levelType: String,
    val unit: String,
    val value: Double
)
