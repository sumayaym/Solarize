package no.uio.ifi.in2000.team39.model.hks

enum class PowerPriceArea(
    val counties: List<String>,
    val mvaRate: Double = 1.25
) {
    NO1(listOf("Oslo", "Østfold", "Innlandet", "Akershus")),
    NO2(listOf("Buskerud", "Telemark", "Agder", "Vestfold", "Rogaland")),
    NO3(listOf("Trøndelag", "Møre og Romsdal")),
    NO4(listOf("Finnmark", "Troms", "Nordland"), mvaRate = 1.00),
    NO5(listOf("Vestland"))
}