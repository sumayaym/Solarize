package no.uio.ifi.in2000.team39.ui.production

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team39.data.settingsForDatabase.DatabaseRepository
import no.uio.ifi.in2000.team39.model.databaseEntities.Home
import no.uio.ifi.in2000.team39.ui.production.model.MonthlyData
import no.uio.ifi.in2000.team39.ui.production.model.MonthlyFrostData
import no.uio.ifi.in2000.team39.ui.production.model.MonthlyPvgisData
import no.uio.ifi.in2000.team39.ui.production.model.Season
import no.uio.ifi.in2000.team39.ui.production.model.SeasonalData
import no.uio.ifi.in2000.team39.ui.production.model.getSeasonForMonth
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ProdscreenViewModel @Inject constructor(
    application: Application,
    private val databaseRepository: DatabaseRepository,
) : AndroidViewModel(application) {

    private val _monthlyProductionData = MutableStateFlow<List<MonthlyData>>(emptyList())
    val monthlyProductionData: StateFlow<List<MonthlyData>> = _monthlyProductionData

    private val _seasonalData = MutableStateFlow<List<SeasonalData>>(emptyList())
    val seasonalData: StateFlow<List<SeasonalData>> = _seasonalData

    private val _selectedDataSource = MutableStateFlow(DataSource.PVGIS) // Default til PVGIS
    val selectedDataSource: StateFlow<DataSource> = _selectedDataSource.asStateFlow()

    private var cachedPvgisSeasonalData: List<SeasonalData> = emptyList()
    private var cachedFrostSeasonalData: List<SeasonalData> = emptyList()


    private val _selectedGraphType = MutableStateFlow(GraphType.COLUMN)
    val selectedGraphType: StateFlow<GraphType> = _selectedGraphType.asStateFlow()

    private val _investment = MutableStateFlow(100000)
    val investment: StateFlow<Int> = _investment.asStateFlow()

    fun increaseInvestment() {
        _investment.value = (_investment.value + 10000).coerceAtMost(500000)
    }

    fun decreaseInvestment() {
        _investment.value = (_investment.value - 10000).coerceAtLeast(10000)
    }


    fun setSelectedGraphType(type: GraphType) {
        _selectedGraphType.value = type
    }

    fun onGraphPreviewClicked(graphType: GraphType, navController: NavController) {
        setSelectedGraphType(graphType)
        navController.navigate("savingGraphDetails")
    }

    fun setSelectedDataSource(dataSource: DataSource) {
        _selectedDataSource.value = dataSource
        _seasonalData.value = when (dataSource) {
            DataSource.PVGIS -> cachedPvgisSeasonalData
            DataSource.FROST -> cachedFrostSeasonalData
        }
        _monthlyProductionData.value = when (dataSource) {
            DataSource.PVGIS -> cachedPvgisSeasonalData.flatMap { it.monthlyData }
                .sortedBy { it.month }

            DataSource.FROST -> cachedFrostSeasonalData.flatMap { it.monthlyData }
                .sortedBy { it.month }
        }
    }

    fun loadDataForHome(home: Home) {
        viewModelScope.launch {
            try {
                val allPvgisMonthlyDataEntities =
                    databaseRepository.getAllMonthlyPVGISDataFromHomeID(home.id)
                val allFrostMonthlyDataEntities =
                    databaseRepository.getAllMonthlyFrostDataFromHomeID(home.id)
                val hksMonthlyDataEntities = databaseRepository.getAllMonthlyHKSData(
                    home.priceArea,
                    LocalDate.now().year - 1
                )
                val frostYearlyEntity = databaseRepository.getFirstFrostDataForHome(home.id)

                // Summer månedlig PVGIS-produksjon for alle tak
                val summedPvgisData = allPvgisMonthlyDataEntities.groupBy { it.month }
                    .map { (month, monthlyDataList) ->
                        val totalProduction = monthlyDataList.sumOf { it.monthlyProduction }
                        val avgPriceEntity = hksMonthlyDataEntities.find { it.month == month }
                        MonthlyPvgisData(
                            month = month,
                            energyProduced = totalProduction,
                            costEquivalent = (totalProduction * (avgPriceEntity?.averagePrice
                                ?: 0.0)).coerceAtLeast(0.0),
                            avgPrice = avgPriceEntity?.averagePrice
                        )
                    }.sortedBy { it.month }

                // Summer månedlig Frost-produksjon for alle tak
                val summedFrostData = allFrostMonthlyDataEntities.groupBy { it.month }
                    .map { (month, monthlyDataList) ->
                        val totalProduction = monthlyDataList.sumOf { it.monthlyProduction }
                        val avgPriceEntity = hksMonthlyDataEntities.find { it.month == month }
                        MonthlyFrostData(
                            month = month,
                            energyProduced = totalProduction,
                            costEquivalent = (totalProduction * (avgPriceEntity?.averagePrice
                                ?: 0.0)).coerceAtLeast(0.0),
                            avgPrice = avgPriceEntity?.averagePrice,
                            avgTemperature = frostYearlyEntity?.avgTemperature,
                            avgSnowCoverage = frostYearlyEntity?.avgSnowCoverage,
                            avgCloudCoverage = frostYearlyEntity?.avgCloudCoverage
                        )
                    }.sortedBy { it.month }

                _monthlyProductionData.value = when (_selectedDataSource.value) {
                    DataSource.PVGIS -> summedPvgisData
                    DataSource.FROST -> summedFrostData
                }

                calculateSeasonalProduction(summedPvgisData) { cachedPvgisSeasonalData = it }
                calculateSeasonalProduction(summedFrostData) { cachedFrostSeasonalData = it }

                if (_selectedDataSource.value == DataSource.PVGIS) {
                    _monthlyProductionData.value = summedPvgisData
                    _seasonalData.value = cachedPvgisSeasonalData
                } else {
                    _monthlyProductionData.value = summedFrostData
                    _seasonalData.value = cachedFrostSeasonalData
                }

            } catch (e: Exception) {
                Log.e("ProdscreenViewModel", "Feil ved henting av data fra database: ${e.message}")
                _monthlyProductionData.value = emptyList()
                cachedFrostSeasonalData = emptyList()
                cachedPvgisSeasonalData = emptyList()
                _seasonalData.value = emptyList()
            }
        }
    }

    private fun calculateSeasonalProduction(
        monthlyData: List<MonthlyData>,
        onSeasonalDataCalculated: (List<SeasonalData>) -> Unit
    ) {
        val groupedData = monthlyData.groupBy { getSeasonForMonth(it.month) }
        val totalYearlyProduction = monthlyData.sumOf { it.energyProduced }
        val totalYearlySavings = monthlyData.sumOf { it.costEquivalent ?: 0.0 }

        val calculatedSeasonalData = groupedData.map { (season, monthlyList) ->
            val seasonTotalEnergy = monthlyList.sumOf { it.energyProduced }
            val seasonTotalSavings = monthlyList.sumOf { it.costEquivalent ?: 0.0 }
            val percentageProduction =
                if (totalYearlyProduction > 0) (seasonTotalEnergy / totalYearlyProduction) * 100 else 0.0
            val percentageSavings =
                if (totalYearlySavings > 0) (seasonTotalSavings / totalYearlySavings) * 100 else 0.0

            SeasonalData(
                season = season,
                totalProduction = seasonTotalEnergy,
                totalSavings = seasonTotalSavings,
                monthlyData = monthlyList,
                percentageOfYearlyProduction = percentageProduction,
                percentageOfYearlySavings = percentageSavings
            )
        }.sortedBy {
            when (it.season) {
                Season.WINTER -> 0
                Season.SPRING -> 1
                Season.SUMMER -> 2
                Season.AUTUMN -> 3
            }
        }
        onSeasonalDataCalculated(calculatedSeasonalData)
    }

    fun resetData() {
        _monthlyProductionData.value = emptyList()
        cachedFrostSeasonalData = emptyList()
        cachedPvgisSeasonalData = emptyList()
        _seasonalData.value = emptyList()
    }

}

enum class GraphType {
    COLUMN,
    LINE
}

enum class DataSource {
    PVGIS,
    FROST
}