package no.uio.ifi.in2000.team39.model.map

import kotlinx.serialization.Serializable

@Serializable
data class PhotonResponse(
    val type: String,
    val features: List<PhotonFeature>
)

@Serializable
data class PhotonFeature(
    val type: String,
    val geometry: PhotonGeometry,
    val properties: PhotonProperties
)

@Serializable
data class PhotonGeometry(
    val type: String,
    val coordinates: List<Double>
)

@Serializable
data class PhotonProperties(
    val name: String? = null,
    val city: String? = null,
    val country: String? = null,
    val postcode: String? = null,
    val street: String? = null,
    val housenumber: String? = null
)