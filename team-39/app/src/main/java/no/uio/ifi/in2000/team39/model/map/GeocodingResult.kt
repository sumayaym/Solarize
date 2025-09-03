package no.uio.ifi.in2000.team39.model.map

import kotlinx.serialization.Serializable

@Serializable
data class GeocodingResult(
    val lat: String,
    val lon: String,
    val displayName: String? = null // // Full adresse eller navn (valgfritt – API returnerer det, men vi ignorerer resten)
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