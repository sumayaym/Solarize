package no.uio.ifi.in2000.team39.data.frost

import no.uio.ifi.in2000.team39.model.frost.Element
import no.uio.ifi.in2000.team39.model.frost.Observation
import no.uio.ifi.in2000.team39.model.frost.Station
import javax.inject.Inject

class FrostRepository @Inject constructor(
    private val frostDataSource: FrostDataSource
) {
    suspend fun fetchNearestStation(lat: Double, lon: Double): Station =
        frostDataSource.getNearestStationIdResponse(lat, lon).data.first()

    suspend fun fetchWeatherObservations(
        stationId: String,
        elementTypes: List<String>,
        startDate: String,
        endDate: String
    ): List<Observation> =
        frostDataSource.getWeatherObservationsResponse(
            stationId,
            elementTypes,
            startDate,
            endDate
        )?.data
            ?: emptyList()

    suspend fun calculateMonthlyWeatherAverages(
        stationId: String,
        startDate: String,
        endDate: String,
    ): MutableMap<String, MutableList<Double>> {

        val observationsResponse = frostDataSource.getWeatherObservationsResponse(
            stationId,
            listOf(
                "mean(air_temperature P1M)",
                "mean(snow_coverage_type P1M)",
                "mean(cloud_area_fraction P1M)"
            ),
            startDate,
            endDate
        )?.data

        val monthlyDataSums = mutableMapOf<String, MutableList<Double>>()
        val monthlyDataCounts = mutableMapOf<String, MutableList<Int>>()

        val elements = listOf("air_temperature", "snow_coverage_type", "cloud_area_fraction")
        elements.forEach { element ->
            monthlyDataSums[element] = MutableList(12) { 0.0 }
            monthlyDataCounts[element] = MutableList(12) { 0 }
        }

        observationsResponse?.forEach { monthlyObservation ->
            val monthString = monthlyObservation.referenceTime.substring(5, 7)
            val month = monthString.toInt() - 1
            monthlyObservation.observations.forEach { element ->
                when (element) {
                    is Element.AirTemperature -> {
                        monthlyDataSums["air_temperature"]!![month] += element.value
                        monthlyDataCounts["air_temperature"]!![month]++
                    }

                    is Element.SnowCoverage -> {
                        monthlyDataSums["snow_coverage_type"]!![month] += element.value
                        monthlyDataCounts["snow_coverage_type"]!![month]++
                    }

                    is Element.CloudAreaFraction -> {
                        monthlyDataSums["cloud_area_fraction"]!![month] += element.value
                        monthlyDataCounts["cloud_area_fraction"]!![month]++
                    }
                }
            }
        }
        val monthlyAverages = mutableMapOf<String, MutableList<Double>>()
        monthlyDataSums.forEach { (elementType, sums) ->
            val counts = monthlyDataCounts[elementType]!!
            val averages = sums.mapIndexed { index, sum ->
                if (counts[index] > 0) sum / counts[index] else Double.NaN
            }.toMutableList()
            monthlyAverages[elementType] = averages
        }
        return monthlyAverages
    }

    fun estimateMonthlySolarEnergy(
        peakPower: Double,
        systemLoss: Double,
        radiationData: List<List<Double>>,
        weatherData: Map<String, List<Double>>
    ): List<Double> {

        val k1 = -0.017237
        val k2 = -0.040465
        val k3 = -0.004702
        val k4 = 0.000149
        val k5 = 0.000170
        val k6 = 0.000005

        val daysPerMonth = listOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

        return List(12) { monthIndex ->
            val hourlyRadiationList = radiationData.getOrElse(monthIndex) { emptyList() }

            val averageTemperatureForMonth =
                checkNaN(weatherData["air_temperature"]?.getOrElse(monthIndex) { 6.0 }, 6.0)

            val averageSnowCoverageForMonth =
                checkNaN(weatherData["snow_coverage_type"]?.getOrElse(monthIndex) { 1.2 }, 0.2)

            val averageCloudCoverageForMonth =
                checkNaN(weatherData["cloud_area_fraction"]?.getOrElse(monthIndex) { 2.0 }, 2.0)

            var monthlyEnergy = 0.0

            for (hourlyG in hourlyRadiationList) {

                val solarKwh = hourlyG / 1000.0
                val temperatureModule = averageTemperatureForMonth + 20.0
                val temperatureForFormula = temperatureModule - 25.0

                val lnSolarKwh = kotlin.math.ln(solarKwh.coerceAtLeast(0.001))

                val effrel = (1.0 +
                        k1 * lnSolarKwh +
                        k2 * lnSolarKwh * lnSolarKwh +
                        k3 * temperatureForFormula +
                        k4 * temperatureForFormula * lnSolarKwh +
                        k5 * temperatureForFormula * lnSolarKwh * lnSolarKwh +
                        k6 * temperatureForFormula * temperatureForFormula).coerceAtLeast(0.0)


                val snowCoverageImpact = 1 - (averageSnowCoverageForMonth / 4.0)
                val cloudCoverageImpact = 1 - (averageCloudCoverageForMonth / 100.0)

                val losses = snowCoverageImpact * cloudCoverageImpact * (1 - systemLoss)

                val hourlyEnergy = solarKwh * peakPower * effrel * losses

                monthlyEnergy += hourlyEnergy
            }
            monthlyEnergy * daysPerMonth[monthIndex]
        }
    }

    private fun checkNaN(value: Double?, defaultValue: Double): Double =
        if (value == null || value.isNaN()) defaultValue else value
}

