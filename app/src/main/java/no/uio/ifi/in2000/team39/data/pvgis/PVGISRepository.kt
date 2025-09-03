package no.uio.ifi.in2000.team39.data.pvgis


import no.uio.ifi.in2000.team39.model.pvgis.HourlyData
import no.uio.ifi.in2000.team39.model.pvgis.PVGISCalculatorResponse
import no.uio.ifi.in2000.team39.model.pvgis.SolarParams
import javax.inject.Inject


class PVGISRepository @Inject constructor(
    private val dataSource: PVGISDataSource
) {
    suspend fun fetchSolarData(params: SolarParams): PVGISCalculatorResponse =
        dataSource.fetchSolarData(params)

    suspend fun fetchMonthlyRadiation(params: SolarParams): List<List<Double>> {
        val hourlyDataList = retrieveHourlyData(params)
        val monthlyList = mergeHourlyDataToMonthlyList(hourlyDataList)

        return (monthlyList)
    }

    private suspend fun retrieveHourlyData(params: SolarParams): List<HourlyData> {
        val response = dataSource.fetchMonthlyRadiationData(params)
        return response.outputs.hourly
    }

    private fun mergeHourlyDataToMonthlyList(hourlyDataList: List<HourlyData>): List<List<Double>> {
        val monthlyList = MutableList<MutableList<Double>>(12) { mutableListOf() }

        hourlyDataList.forEach { hourlyData ->
            val month = hourlyData.month - 1
            if (month in 0..11) {
                monthlyList[month].add(hourlyData.irradianceOnTiltedSurface)
            }
        }

        return monthlyList
    }
}
