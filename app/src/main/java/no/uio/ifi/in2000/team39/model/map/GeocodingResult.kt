package no.uio.ifi.in2000.team39.model.map

import kotlinx.serialization.Serializable

@Serializable
data class GeocodingResult(
    val lat: String,
    val lon: String,
    val displayName: String? = null,
    val address: AddressDetails? = null
)

@Serializable
data class AddressDetails(
    val road: String? = null,
    val houseNumber: String? = null,
    val postcode: String? = null,
    val city: String? = null,
    val town: String? = null,
    val village: String? = null,
    val county: String? = null,
)

/**
 * Representerer ett søkeresultat fra geokodingstjenesten (Nominatim/OpenStreetMap).
 * Feltene samsvarer med JSON-svaret fra API-et.
 *
 * Hvorfor er lat og lon String, og ikke Double?
 * Nominatim (API-et) returnerer koordinater som tekst. Derfor
 * må vi hente dem som String, og heller konvertere til Double senere
 * som i geocodeAddress()
 */