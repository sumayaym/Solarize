package no.uio.ifi.in2000.team39.data.pvgis



import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PVGISResponse( // hele responsen
    val outputs: Outputs
)

@Serializable
data class Outputs( // outputs har to "nøkler" totals og monthly
    val totals: TotalsWrapper,
    val monthly: MonthlyWrapper
)

@Serializable
data class TotalsWrapper( // går i totals "wrapperen"
    val fixed: FixedData
)

@Serializable
data class MonthlyWrapper(  // går inn i monthly wrapperen
    val fixed: List<MonthlyData>
)

// output<totals<fixed

// bruker serialname fordi kotlin ikke støtter navn med parantes som api-reponsen har
@Serializable
data class FixedData(
    val E_y: Double,
    val E_d: Double,
    val E_m: Double,
    @SerialName("H(i)_d")
    val H_i_d: Double,
    @SerialName("H(i)_m")
    val H_i_m: Double,
    @SerialName("H(i)_y")
    val H_i_y: Double,
    val SD_m: Double,
    val SD_y: Double,
    val l_aoi: Double,
    val l_spec: String,
    val l_tg: Double,
    val l_total: Double
)


// output<monthly<fixed
@Serializable
data class MonthlyData(
    val month: Int,
    val E_d: Double,
    val E_m: Double,
    @SerialName("H(i)_d")
    val H_i_d: Double,
    @SerialName("H(i)_m")
    val H_i_m: Double,
    val SD_m: Double
)
