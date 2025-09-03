package no.uio.ifi.in2000.team39.data.hks

import android.util.Log
import no.uio.ifi.in2000.team39.model.hks.HKSParams
import no.uio.ifi.in2000.team39.model.hks.HKSPriceData
import javax.inject.Inject

class HKSRepository @Inject constructor(
    private val dataSource: HKSDataSource
) {

    private suspend fun fetchElectricityPrices(params: HKSParams): List<HKSPriceData> {
        return dataSource.fetchElectricityPrices(params)
    }

    suspend fun calculateMonthlyAveragePrices(params: HKSParams): List<Double> {
        val year = params.year.toInt()

        val monthlyPrices = MutableList(12) { mutableListOf<Double>() }

        for (month in 1..12) {
            val dayParams = HKSParams(
                year = year.toString(),
                month = "%02d".format(month),
                day = 10.toString(),
                area = params.area
            )
            try {
                val prices = fetchElectricityPrices(dayParams)
                prices.forEach { monthlyPrices[month - 1].add(it.nokPerKWh) }
            } catch (e: Exception) {
                Log.e(
                    "HKSRepository",
                    "Feil ved henting av priser for $year-${"%02d".format(month)}-15}: ${e.message}"
                )
            }
        }

        return monthlyPrices.map {
            if (it.isNotEmpty()) it.average() else 1.0
        }


    }
}
