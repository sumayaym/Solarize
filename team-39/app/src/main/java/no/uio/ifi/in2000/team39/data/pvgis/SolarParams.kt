package no.uio.ifi.in2000.team39.data.pvgis



data class SolarParams(
    val lat: Double,
    val lon: Double,
    val peakPower: Int = 5,
    val angle: Int = 30,
    val aspect: Int = 180,
    val loss: Int = 14,
    val pvtech: String = "crystSi", // antar at panelet er laget av sisilium, ettersom 90% av verdens paneler er laget avdet
    val mounting: String = "building" // hvor solpanelene blir implementert
)
