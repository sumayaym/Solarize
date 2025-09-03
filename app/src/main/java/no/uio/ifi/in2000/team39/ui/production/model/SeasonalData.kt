package no.uio.ifi.in2000.team39.ui.production.model

data class SeasonalData(
    val season: Season,
    val totalProduction: Double,
    val totalSavings: Double,
    val monthlyData: List<MonthlyData>,
    val percentageOfYearlyProduction: Double = 0.0,
    val percentageOfYearlySavings: Double = 0.0,
)

enum class Season(val displayName: String) {
    WINTER("Vinter"),
    SPRING("Vår"),
    SUMMER("Sommer"),
    AUTUMN("Høst")

}

fun getSeasonForMonth(month: Int): Season {
    return when (month) {
        12, 1, 2 -> Season.WINTER
        3, 4, 5 -> Season.SPRING
        6, 7, 8 -> Season.SUMMER
        9, 10, 11 -> Season.AUTUMN
        else -> throw IllegalArgumentException("Ugyldig måned: $month")
    }
}