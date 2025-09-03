package no.uio.ifi.in2000.team39.model.frost

import kotlinx.serialization.Serializable

@Serializable
data class FrostStationResponse(
    val data: List<Station>,
    val totalItemCount: Int? = null
)

@Serializable
data class Station(
    val id: String,
    val name: String,
    val geometry: Geometry,
    val distance: Double? = null,
    val validFrom: String? = null,
)

@Serializable
data class Geometry(
    val coordinates: List<Double>
)