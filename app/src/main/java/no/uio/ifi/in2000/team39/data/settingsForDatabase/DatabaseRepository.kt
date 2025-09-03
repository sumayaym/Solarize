package no.uio.ifi.in2000.team39.data.settingsForDatabase

import android.util.Log
import no.uio.ifi.in2000.team39.data.frost.FrostRepository
import no.uio.ifi.in2000.team39.data.hks.HKSRepository
import no.uio.ifi.in2000.team39.data.pvgis.PVGISRepository
import no.uio.ifi.in2000.team39.data.settingsForDatabase.dao.FrostDao
import no.uio.ifi.in2000.team39.data.settingsForDatabase.dao.HKSDao
import no.uio.ifi.in2000.team39.data.settingsForDatabase.dao.HomeDao
import no.uio.ifi.in2000.team39.data.settingsForDatabase.dao.PVGISDao
import no.uio.ifi.in2000.team39.data.settingsForDatabase.dao.RoofDao
import no.uio.ifi.in2000.team39.model.databaseEntities.FrostDataEntity
import no.uio.ifi.in2000.team39.model.databaseEntities.FrostMonthlyDataEntity
import no.uio.ifi.in2000.team39.model.databaseEntities.HKSMonthlyDataEntity
import no.uio.ifi.in2000.team39.model.databaseEntities.Home
import no.uio.ifi.in2000.team39.model.databaseEntities.PVGISDataEntity
import no.uio.ifi.in2000.team39.model.databaseEntities.PVGISMonthlyDataEntity
import no.uio.ifi.in2000.team39.model.databaseEntities.RoofEntity
import no.uio.ifi.in2000.team39.model.hks.HKSParams
import no.uio.ifi.in2000.team39.model.hks.PowerPriceArea
import no.uio.ifi.in2000.team39.model.pvgis.SolarParams
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.max

class DatabaseRepository @Inject constructor(
    private val homeDao: HomeDao,
    private val frostDataDao: FrostDao,
    private val pvgisDao: PVGISDao,
    private val hksDao: HKSDao,
    private val roofDao: RoofDao,
    private val frostRepository: FrostRepository,
    private val pvgisRepository: PVGISRepository,
    private val hksRepository: HKSRepository
) {


    private val maxeon3PanelLength = 1.7
    private val maxeon3PanelWidth = 1.0

    // Panels orientation. Solarpanels are not complete square.
    private fun calculateMaxPanels(roofLength: Double, roofWidth: Double): Int {

        val panelsLyingLength = floor(roofLength / maxeon3PanelLength)
        val panelsLyingWidth = floor(roofWidth / maxeon3PanelWidth)
        val maxPanelsLying = (panelsLyingLength * panelsLyingWidth).toInt()

        val panelsStandingLength = floor(roofLength / maxeon3PanelWidth)
        val panelsStandingWidth = floor(roofWidth / maxeon3PanelLength)
        val maxPanelsStanding = (panelsStandingLength * panelsStandingWidth).toInt()

        return max(maxPanelsLying, maxPanelsStanding)
    }

    suspend fun createNewHomeWithRoof(
        address: String,
        latitude: Double,
        longitude: Double,
        length: Double,
        width: Double,
        angle: Double,
        direction: String,
        priceArea: String
    ): Long {
        val newHome = Home(
            address = address,
            latitude = latitude,
            longitude = longitude,
            priceArea = priceArea
        )
        val homeId = homeDao.insertHome(newHome)

        val maxPanels = calculateMaxPanels(length, width)

        val newRoofSurface = RoofEntity(
            homeId = homeId.toInt(),
            roofNumber = 1,
            roofLength = length,
            roofWidth = width,
            roofDirection = direction,
            roofAngle = angle,
            panels = maxPanels
        )
        roofDao.insertRoofSurface(newRoofSurface)
        performApiCallsForRoofSurface(newHome, newRoofSurface, homeId)
        return homeId
    }


    suspend fun findHomeByAddress(address: String): Home? {
        return try {
            homeDao.getHomeByAddress(address)
        } catch (e: Exception) {
            Log.e("DatabaseRepository", "Feil ved søk etter bolig med adresse $address", e)
            null
        }
    }

    suspend fun addRoofSurface(
        homeId: Int,
        length: Double,
        width: Double,
        direction: String,
        angle: Double,
        year: Int
    ) {
        val home = homeDao.getHomeById(homeId) ?: return
        val existingSurfaces = roofDao.getAllRoofSurfacesForHome(homeId)
        val nextSurfaceNumber = if (existingSurfaces.size < 4) {
            existingSurfaces.size + 1
        } else {
            1
        }


        val maxPanels = calculateMaxPanels(length, width)

        val newRoofSurface = RoofEntity(
            homeId = homeId,
            roofNumber = nextSurfaceNumber,
            roofLength = length,
            roofWidth = width,
            roofDirection = direction,
            roofAngle = angle,
            panels = maxPanels
        )
        val roofId = roofDao.insertRoofSurface(newRoofSurface)
        performApiCallsForRoofSurface(home, newRoofSurface, roofId)
        checkOrFetchPowerPrices(home, year)
    }


    private suspend fun performApiCallsForRoofSurface(home: Home, roof: RoofEntity, homeId: Long) {
        val numberOfPanels = roof.panels
        savePVGISDataForRoofSurface(
            home,
            numberOfPanels.toDouble(),
            roof.roofAngle,
            roof.roofDirection,
            homeId
        )
        saveFrostDataForRoofSurface(
            home,
            numberOfPanels.toDouble(),
            roof.roofAngle,
            roof.roofDirection,
            homeId
        )
    }

    private suspend fun savePVGISDataForRoofSurface(
        home: Home,
        roofArea: Double,
        roofAngle: Double,
        roofDirection: String,
        roofId: Long
    ) {
        try {
            val startTime = System.currentTimeMillis()
            val solarParamsForApi = SolarParams(
                lat = home.latitude,
                lon = home.longitude,
                angle = roofAngle.toInt(),
                aspect = roofDirection.toIntOrNull() ?: 180
            )

            Log.d("DatabaseRepository", "Kaller PVGIS API med parametere: $solarParamsForApi")
            val pvgisResponse = pvgisRepository.fetchSolarData(solarParamsForApi)
            val endTimeApi = System.currentTimeMillis()
            Log.d(
                "DatabaseRepository",
                "PVGIS API respons mottatt etter ${endTimeApi - startTime} ms"
            )

            val outputs = pvgisResponse.outputs

            val pvgisYearlyData = PVGISDataEntity(
                roofId = roofId,
                latitude = home.latitude,
                longitude = home.longitude,
                yearlyProduction = outputs.totals.fixed.yearlyEnergyProduction * roofArea
            )
            val pvgisYearlyId = pvgisDao.insertPVGISData(pvgisYearlyData)
            Log.d("DatabaseRepository", "PVGIS Yearly ID for roof surface: $pvgisYearlyId")

            val monthlyApiData = outputs.monthly.fixed
            val monthlyEntities = monthlyApiData.map { monthlyEntry ->
                val month = monthlyEntry.month
                val monthlyProductionPerm2 =
                    monthlyEntry.monthlyEnergyProduction
                PVGISMonthlyDataEntity(
                    roofId = roofId,
                    pvgisDataId = pvgisYearlyId.toInt(),
                    parentLatitude = home.latitude,
                    parentLongitude = home.longitude,
                    month = month,
                    monthlyProduction = monthlyProductionPerm2 * roofArea
                )
            }

            if (monthlyEntities.isNotEmpty()) {
                pvgisDao.insertAllPVGISMonthlyData(monthlyEntities)
            } else {
                Log.w(
                    "DatabaseRepository",
                    "Månedlig PVGIS-data manglet for takoverflate ved ${home.latitude}, ${home.longitude}"
                )
            }

        } catch (e: Exception) {
            Log.e(
                "DatabaseRepository",
                "Feil ved henting og lagring av PVGIS-data for takoverflate ved ${home.latitude}, ${home.longitude}",
                e
            )
        }
    }

    private suspend fun saveFrostDataForRoofSurface(
        home: Home,
        roofArea: Double,
        roofAngle: Double,
        roofDirection: String,
        roofId: Long
    ) {
        try {
            val solarParamsForApi = SolarParams(
                lat = home.latitude,
                lon = home.longitude,
                angle = roofAngle.toInt(),
                aspect = roofDirection.toIntOrNull() ?: 180
            )

            val station = frostRepository.fetchNearestStation(home.latitude, home.longitude)

            val radiationList = pvgisRepository.fetchMonthlyRadiation(solarParamsForApi)
            Log.d(
                "DatabaseRepository",
                "Verdien av radiationList etter API-kall for takoverflate: $radiationList"
            )

            var weatherData: MutableMap<String, MutableList<Double>> = mutableMapOf()

            try {
                weatherData = frostRepository.calculateMonthlyWeatherAverages(
                    station.id,
                    startDate = "2024-01-01",
                    endDate = "2025-01-01"
                )
            } catch (e: Exception) {
                Log.e(
                    "DatabaseRepository",
                    "Feil ved henting av userdata for takoverflate ved ${home.latitude}, ${home.longitude}",
                    e
                )
            }

            val frostProduction = frostRepository.estimateMonthlySolarEnergy(
                peakPower = 0.4,
                systemLoss = 0.09,
                radiationData = radiationList,
                weatherData = weatherData
            ).map { it * roofArea }

            Log.d(
                "DatabaseRepository",
                "Verdien av frostProduction etter API-kall for takoverflate: $frostProduction"
            )
            val yearlyProduction = frostProduction.sum()


            val avgTemperature = weatherData["air_temperature"]?.average()?.takeIf { it.isFinite() }
            val avgSnowCoverage =
                weatherData["snow_coverage_type"]?.average()?.takeIf { it.isFinite() }
            val avgCloudCoverage =
                weatherData["cloud_area_fraction"]?.average()?.takeIf { it.isFinite() }

            val frostData = FrostDataEntity(
                roofId = roofId,
                latitude = home.latitude,
                longitude = home.longitude,
                avgTemperature = avgTemperature,
                avgSnowCoverage = avgSnowCoverage,
                avgCloudCoverage = avgCloudCoverage,
                yearlyProduction = yearlyProduction
            )
            val frostYearlyId = frostDataDao.insertFrostData(frostData)

            val monthlyEntities = frostProduction.mapIndexed { index, monthlyProduction ->
                val month = index + 1
                FrostMonthlyDataEntity(
                    roofId = roofId,
                    frostDataId = frostYearlyId.toInt(),
                    parentLatitude = home.latitude,
                    parentLongitude = home.longitude,
                    month = month,
                    monthlyProduction = monthlyProduction
                )
            }

            if (monthlyEntities.isNotEmpty()) {
                frostDataDao.insertAllFrostMonthlyData(monthlyEntities)
            } else {
                Log.w(
                    "DatabaseRepository",
                    "Månedlig Frost-data manglet for takoverflate ved ${home.latitude}, ${home.longitude}"
                )
            }

        } catch (e: Exception) {
            Log.w(
                "DatabaseRepository",
                "Frost API feilet under lagring for takoverflate ved ${home.latitude}, ${home.longitude}: ${e.message}"
            )
        }
    }

    private suspend fun fetchAndInsertPowerPrices(year: Int, area: String) {
        try {
            val hksParams = HKSParams(year = year.toString(), area = area)
            val prices = hksRepository.calculateMonthlyAveragePrices(hksParams)
            val powerPriceAreaEnum = PowerPriceArea.entries.find { it.name == area }

            val monthlyEntities = prices.mapIndexed { index, monthlyProduction ->
                val mvaRate = powerPriceAreaEnum?.mvaRate ?: 1.25
                val month = index + 1
                HKSMonthlyDataEntity(
                    area = area,
                    year = year,
                    month = month,
                    averagePrice = monthlyProduction * mvaRate
                )
            }
            if (monthlyEntities.isNotEmpty()) {
                hksDao.insertAllMonthlyData(monthlyEntities)
            } else {
                Log.w("DatabaseRepository", "Månedlig Strøm-data manglet for $area")
            }
        } catch (e: Exception) {
            Log.e("DatabaseRepository", "Feil ved henting av strømpriser", e)
        }
    }

    private suspend fun checkOrFetchPowerPrices(home: Home, year: Int) {
        val powerPriceArea = home.priceArea
        val existingPriceCount = hksDao.getAllMonthlyDataForYear(powerPriceArea, year)
        if (existingPriceCount.count() < 12) {
            Log.i("DatabaseRepository", "Henter strømpriser for $powerPriceArea da data mangler.")
            try {
                fetchAndInsertPowerPrices(year, powerPriceArea)
            } catch (e: Exception) {
                Log.e("DatabaseRepository", "Feil ved henting av strømpriser", e)
            }
        } else {
            Log.i("DatabaseRepository", "Strømpriser for $powerPriceArea finnes allerede.")
        }
    }


    suspend fun getAllRoofSurfacesForHome(homeId: Int): List<RoofEntity> =
        roofDao.getAllRoofSurfacesForHome(homeId)

    suspend fun getAllHomes(): List<Home> {
        return homeDao.getAllHomes()
    }


    suspend fun getFirstFrostDataForHome(homeId: Int): FrostDataEntity? {
        return frostDataDao.getFirstFrostDataForHome(homeId)
    }

    suspend fun getAllMonthlyHKSData(area: String, year: Int): List<HKSMonthlyDataEntity> {
        try {
            val dataForYear = hksDao.getAllMonthlyDataForYear(area, year)
            if (dataForYear.isNotEmpty()) {
                return dataForYear
            } else {
                try {
                    fetchAndInsertPowerPrices(year, area)
                    return hksDao.getAllMonthlyDataForYear(area, year)
                } catch (ePrevious: Exception) {
                    Log.e(
                        "DatabaseRepository",
                        "Feil ved henting av HKS data for $area i ${year - 1}",
                        ePrevious
                    )
                    return emptyList()
                }
            }
        } catch (e: Exception) {
            Log.e("DatabaseRepository", "Feil ved henting av HKS data for $area i $year", e)
            return emptyList()
        }
    }

    suspend fun getAllMonthlyPVGISDataFromHomeID(homeId: Int): List<PVGISMonthlyDataEntity> {
        return pvgisDao.getAllMonthlyPVGISDataForHome(homeId)
    }

    suspend fun getAllMonthlyFrostDataFromHomeID(homeId: Int): List<FrostMonthlyDataEntity> {
        return frostDataDao.getAllMonthlyFrostDataForHome(homeId)
    }

    suspend fun deleteHome(home: Home) {
        homeDao.deleteHome(home)
        Log.i("DatabaseRepository", "Bolig med ID ${home.id} slettet.")
    }

    suspend fun deleteAllData() {
        homeDao.deleteAllHomes()
    }

/* These were never used
    suspend fun saveFrostData(frostData: FrostDataEntity) {
        frostDataDao.insertFrostData(frostData)
    }

    suspend fun savePVGISYearlyData(pvgisData: PVGISDataEntity) {
        pvgisDao.insertPVGISData(pvgisData)
    }

    suspend fun getFrostDataForRoof(roofId: Int): FrostDataEntity? {
        return frostDataDao.getFrostDataForRoof(roofId)
    }

    suspend fun getPVGISYearlyDataForRoof(roofId: Int): PVGISDataEntity? {
        return pvgisDao.getPVGISDataForRoof(roofId)
    }

    suspend fun getHome(lat: Double, lon: Double): Home? {
        return homeDao.getHome(lat, lon)
    }

    suspend fun getTotalYearlyPVGISProductionForHome(homeId: Int): Double {
        val allPvgisData = pvgisDao.getAllPVGISDataForHome(homeId)
        return allPvgisData.sumOf { it.yearlyProduction }
    }
 */

}

