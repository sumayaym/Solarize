package no.uio.ifi.in2000.team39.ui.production

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team39.R
import no.uio.ifi.in2000.team39.model.databaseEntities.Home
import no.uio.ifi.in2000.team39.ui.HomeUiState
import no.uio.ifi.in2000.team39.ui.SharedHomeViewModel
import no.uio.ifi.in2000.team39.ui.components.HomeSelectorBarDropdown
import no.uio.ifi.in2000.team39.ui.components.WelcomeScreen
import no.uio.ifi.in2000.team39.ui.production.components.GraphPreviewCard
import no.uio.ifi.in2000.team39.ui.production.components.ProfitSummaryCard
import no.uio.ifi.in2000.team39.ui.production.components.ProgressIndicator
import no.uio.ifi.in2000.team39.ui.production.components.StatsCard
import no.uio.ifi.in2000.team39.ui.production.funfactcards.FunFactCardDeck
import no.uio.ifi.in2000.team39.ui.production.model.MonthNames
import no.uio.ifi.in2000.team39.ui.production.model.MonthlyData
import no.uio.ifi.in2000.team39.ui.production.savinggraph.SavingGraph
import no.uio.ifi.in2000.team39.ui.production.savinggraph.SavingLineGraph
import java.time.LocalDate
import kotlin.math.ceil


@Composable
fun ProdScreen(
    viewModel: ProdscreenViewModel = hiltViewModel(),
    sharedHomeViewModel: SharedHomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val monthlyData by viewModel.monthlyProductionData.collectAsState()
    val selectedHome by sharedHomeViewModel.selectedHome.collectAsState()
    val homesState by sharedHomeViewModel.homes.collectAsState()
    val selectedDataSource by viewModel.selectedDataSource.collectAsState()
    val investment by viewModel.investment.collectAsState()
    var homes by remember { mutableStateOf<List<Home>>(emptyList()) }
    var isHomes by remember { mutableStateOf(false) }

    val yearlySavings = monthlyData.sumOf { it.costEquivalent ?: 0.0 }
    val payBackYears =
        if (yearlySavings > 0) ceil(investment.toDouble() / yearlySavings).toInt() else 0

    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val month = getMonthValue()



    LaunchedEffect(Unit) {
        sharedHomeViewModel.loadHomes()
    }

    LaunchedEffect(selectedHome) {
        selectedHome?.let { viewModel.loadDataForHome(it) } ?: viewModel.resetData()
    }

    when (homesState) {

        HomeUiState.Loading -> {
            CircularProgressIndicator()
        }

        is HomeUiState.Success -> {
            homes = (homesState as HomeUiState.Success<List<Home>>).data
            if (homes.isEmpty()) {
                isHomes = true
                Text(
                    "Ingen boliger er lagt til.",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        is HomeUiState.Error -> {
            val errorMessage =
                (homesState as HomeUiState.Error).message ?: "Feil ved lasting av boliger"
            Text(
                errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }

        is HomeUiState.Idle -> {

        }
    }

    if (isHomes) {
        WelcomeScreen()
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(70.dp))
            Image(
                painter = painterResource(id = R.drawable.background_house),
                contentDescription = "Bakgrunnsbilde",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .wrapContentSize()
                    .fillMaxSize()
            )

            Spacer(modifier = Modifier.height(1000.dp))
        }
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(top = 50.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                HomeSelectorBarDropdown(
                    selectedHome = selectedHome,
                    homes = homes,
                    onHomeSelected = { sharedHomeViewModel.selectHome(it) },
                    onDeleteHome = { sharedHomeViewModel.deleteHome(it) },
                    showSnackBar = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Legg til bolig via kartskjermen")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(240.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "PVGIS",
                        modifier = Modifier
                            .padding(5.dp)
                            .clickable { viewModel.setSelectedDataSource(DataSource.PVGIS) },
                        color = MaterialTheme.colorScheme.primary
                    )
                    Switch(
                        checked = selectedDataSource == DataSource.FROST,
                        onCheckedChange = { viewModel.setSelectedDataSource(if (it) DataSource.FROST else DataSource.PVGIS) }
                    )
                    Text(
                        "FROST",
                        modifier = Modifier
                            .padding(5.dp)
                            .clickable { viewModel.setSelectedDataSource(DataSource.FROST) },
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                        GraphPreviewCard(
                            title = getCurrentMonthMonthlySavedData(monthlyData),
                            subTitle = "Spares i ${MonthNames.getName(month)}",
                            modifier = Modifier.fillMaxWidth(),
                            graphContent = {
                                if (monthlyData.isNotEmpty()) {
                                    SavingGraph(
                                        monthlyCostData = monthlyData,
                                        showAxis = false,
                                        columnThickness = 4.dp,
                                        zoomState = rememberVicoZoomState(zoomEnabled = false)
                                    )
                                } else {
                                    Text(
                                        "Laster graf...",
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            onClick = {
                                viewModel.onGraphPreviewClicked(
                                    GraphType.COLUMN,
                                    navController
                                )
                            }
                        )
                    }
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        GraphPreviewCard(
                            title = getCurrentMonthKwhData(monthlyData),
                            subTitle = "Produseres i ${MonthNames.getName(month)}",
                            graphContent = {
                                if (monthlyData.isNotEmpty()) {
                                    SavingLineGraph(
                                        monthlyCostData = monthlyData,
                                        showAxis = false,
                                        pointSpacing = 4.dp,
                                        zoomState = rememberVicoZoomState(zoomEnabled = false)
                                    )
                                } else {
                                    Text(
                                        "Laster graf...",
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            onClick = {
                                viewModel.onGraphPreviewClicked(
                                    GraphType.LINE,
                                    navController
                                )
                            }
                        )

                    }
                }
                Row {
                    if (monthlyData.isNotEmpty()) {
                        val monthlySumProduction = monthlyData.sumOf { it.energyProduced }
                        val percentage = if (monthlySumProduction > 0) {
                            monthlyData.take(month).sumOf { it.energyProduced }
                                .toFloat() * 100 / monthlySumProduction.toFloat()
                        } else 0f
                        StatsCard(
                            title = "Årlig produksjon",
                            value = "%.2f kWh".format(monthlySumProduction),
                            infoTitle = "Hva er den årlige produksjonen?",
                            infoMessage = "Så mye kan du forvente å produsere hvert år. Etter at ${
                                MonthNames.getName(
                                    month
                                )
                            } måned er over, er omtrent ${"%.1f".format(percentage)}% av det totale årlige produksjonen blitt produsert.",
                            modifier = Modifier.fillMaxWidth(),
                            extraContent = {
                                Spacer(modifier = Modifier.height(5.dp))
                                ProgressIndicator(
                                    percentage = percentage,
                                    strokeWidth = 10f,
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(80.dp)
                                )
                            }
                        )
                    }
                }

                Row {
                    if (monthlyData.isNotEmpty()) {
                        val monthlySum = monthlyData.sumOf { it.costEquivalent ?: 0.0 }
                        val percentage = if (monthlySum > 0) {
                            monthlyData.take(month).sumOf { it.costEquivalent ?: 0.0 }
                                .toFloat() * 100 / monthlySum.toFloat()
                        } else 0f
                        StatsCard(
                            title = "Årlig besperalse",
                            value = "%.2f kr".format(monthlySum),
                            infoTitle = "Hva er den årlige besperalsen?",
                            infoMessage = "Så mye kan du forvente å spare hvert år. Etter at ${
                                MonthNames.getName(
                                    month
                                )
                            } måned er over, er omtrent ${"%.1f".format(percentage)}% av det totale årlige beløpet inntjent.",
                            modifier = Modifier.fillMaxWidth(),
                            extraContent = {
                                Spacer(modifier = Modifier.height(5.dp))
                                ProgressIndicator(
                                    percentage = percentage,
                                    strokeWidth = 10f,
                                    indicatorColor = Color.Green,
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(80.dp)
                                )
                            }
                        )
                    }
                }
                Row {
                    if (monthlyData.isNotEmpty()) {
                        val monthlySum = monthlyData.sumOf { it.energyProduced }
                        val co2 = calculateCO2(monthlySum, 0.273)
                        val percentage = if (co2 > 0.0) {
                            co2.toFloat() * 100 / 6000
                        } else 0f
                        StatsCard(
                            title = "Indirekte CO2 besperalse",
                            value = "%.2f kg CO2".format(co2),
                            infoTitle = "CO2 besparelse?",
                            infoMessage = "JA! I Europa ligger gjennomsnittet på 273 gram CO2-ekvivalenter per kWh for utslipp. Du kan derfor spare Europa for flere kg med utslipp. I tillegg kan du synke ditt årlige utslipp med ${
                                "%.1f".format(
                                    percentage
                                )
                            }%! En gjennomsnittlig nordmann slipper ut 6 tonn. ",
                            modifier = Modifier.fillMaxWidth(),
                            extraContent = {
                                Spacer(modifier = Modifier.height(5.dp))
                                ProgressIndicator(
                                    percentage = percentage,
                                    strokeWidth = 10f,
                                    indicatorColor = Color.Red,
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(80.dp)
                                )
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    if (getCurrentMonthMonthlyData(monthlyData) != null) {
                        FunFactCardDeck(
                            monthlyData = getCurrentMonthMonthlyData(monthlyData)!!,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (monthlyData.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Estimert tilbakebetalingstid",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "$payBackYears år",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    IconButton(
                                        onClick = { viewModel.decreaseInvestment() },
                                        enabled = investment > 10000
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.remove_icon),
                                            contentDescription = "Reduser kostnad",
                                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            "Kostnad for solceller",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            "$investment kr",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.increaseInvestment() },
                                        enabled = investment < 500000
                                    ) {
                                        Icon(
                                            Icons.Filled.Add,
                                            contentDescription = "Øk kostnad",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Årlig besparelse: %.0f kr".format(yearlySavings),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                ProfitSummaryCard(
                    navController = navController,
                    yearlySavings = yearlySavings
                )


            }
            Spacer(modifier = Modifier.height(5.dp))
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
        )
    }
}


fun getCurrentMonthMonthlySavedData(monthlyData: List<MonthlyData>): String {
    if (monthlyData.isEmpty()) return "Data ikke tilgjengelig"
    val currentMonthValue = getMonthValue()

    val currentMonthData = monthlyData.find {
        it.month == currentMonthValue
    }

    return if (currentMonthData != null) {
        "${"%.2f".format(currentMonthData.costEquivalent)} kr"
    } else {
        "Data ikke tilgjengelig"
    }
}

fun getCurrentMonthMonthlyData(monthlyData: List<MonthlyData>): MonthlyData? {
    if (monthlyData.isEmpty()) return null
    val currentMonthValue = getMonthValue()

    val currentMonthData = monthlyData.find {
        it.month == currentMonthValue
    }
    return if (currentMonthData != null)
        currentMonthData
    else
        null


}

fun getCurrentMonthKwhData(monthlyData: List<MonthlyData>): String {
    if (monthlyData.isEmpty()) return "Data ikke tilgjengelig"
    val currentMonthValue = getMonthValue()

    val currentMonthData = monthlyData.find {
        it.month == currentMonthValue
    }

    return if (currentMonthData != null) {
        "${"%.2f".format(currentMonthData.energyProduced)} kWh"
    } else {
        "Data ikke tilgjengelig"
    }
}

fun getMonthValue(): Int = LocalDate.now().month.value

fun calculateCO2(kwh: Double, co2PerKwh: Double) = kwh * co2PerKwh


