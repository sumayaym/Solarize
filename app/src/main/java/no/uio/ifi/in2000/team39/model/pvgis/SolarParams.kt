package no.uio.ifi.in2000.team39.model.pvgis


data class SolarParams(

    val lat: Double,
    val lon: Double,
    val peakPower: Double = 0.4,
    val angle: Int = 30,
    val aspect: Int = 180,
    val loss: Int = 14,
    val pvtech: String = "crystSi", // Most used in the world. Also MAXEON 3
    val mounting: String = "building",
    val month: Int = 0
)
