package no.uio.ifi.in2000.team39.ui.production.model


object MonthNames {
    private val monthMap = mapOf(
        1 to "Januar", 2 to "Februar", 3 to "Mars", 4 to "April",
        5 to "Mai", 6 to "Juni", 7 to "Juli", 8 to "August",
        9 to "September", 10 to "Oktober", 11 to "November", 12 to "Desember"
    )

    fun getName(number: Int): String = monthMap[number] ?: "Ukjent"
}
