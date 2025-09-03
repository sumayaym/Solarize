package no.uio.ifi.in2000.team39.ui.production.savinggraph

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import no.uio.ifi.in2000.team39.ui.production.GraphType
import no.uio.ifi.in2000.team39.ui.production.ProdscreenViewModel
import no.uio.ifi.in2000.team39.ui.production.components.MonthlyDetailCard
import no.uio.ifi.in2000.team39.ui.production.components.ProgressIndicator
import no.uio.ifi.in2000.team39.ui.production.components.StatsCard
import no.uio.ifi.in2000.team39.ui.production.model.MonthNames
import no.uio.ifi.in2000.team39.ui.production.model.getSeasonForMonth
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsGraphDetailScreen(
    viewModel: ProdscreenViewModel,
    navController: NavController,
) {
    val monthlyData by viewModel.monthlyProductionData.collectAsState()
    val seasonalProduction by viewModel.seasonalData.collectAsState()

    val initialGraph by viewModel.selectedGraphType.collectAsState()
    var selectedGraph by remember { mutableStateOf(initialGraph) }

    val tabItems = listOf(GraphType.COLUMN, GraphType.LINE)
    val selectedIndex = remember { mutableIntStateOf(initialGraph.ordinal) }

    val zoomState = rememberVicoZoomState(zoomEnabled = true)

    val maxPrice = monthlyData.takeIf { it.isNotEmpty() }?.maxByOrNull { it.costEquivalent!! }
    val minPrice = monthlyData.takeIf { it.isNotEmpty() }?.minByOrNull { it.costEquivalent!! }

    val maxProduction = monthlyData.takeIf { it.isNotEmpty() }?.maxByOrNull { it.energyProduced }
    val minProduction = monthlyData.takeIf { it.isNotEmpty() }?.minByOrNull { it.energyProduced }

    val currentMonth = LocalDate.now().month.value
    var currentSeasonIndex by remember { mutableIntStateOf(-1) }

    val currentSeasonData = seasonalProduction.getOrNull(currentSeasonIndex)

    LaunchedEffect(seasonalProduction) {
        currentSeasonIndex = if (seasonalProduction.isEmpty()) {
            -1
        } else {
            seasonalProduction.map { it.season }
                .indexOf(getSeasonForMonth(currentMonth))
                .coerceIn(0, seasonalProduction.lastIndex)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detaljert ${if (selectedGraph == GraphType.COLUMN) "besparelse" else "produksjon"}",
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (navController.previousBackStackEntry != null) {
                            navController.popBackStack()
                        } else {
                            navController.navigate("production")
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Tilbake",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                TabRow(
                    selectedTabIndex = selectedIndex.intValue,
                    indicator = { tabPositions ->
                        if (selectedIndex.intValue < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedIndex.intValue]),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.background
                ) {
                    tabItems.forEachIndexed { index, graphType ->
                        Tab(
                            selected = selectedIndex.intValue == index,
                            onClick = {
                                selectedIndex.intValue = index
                                selectedGraph = graphType
                                viewModel.setSelectedGraphType(graphType)
                            },
                            text = {
                                Text(
                                    text = if (graphType == GraphType.COLUMN) "Besparelse" else "Produksjon",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                AnimatedContent(
                    targetState = selectedGraph,
                    transitionSpec = {
                        if (targetState.ordinal > initialState.ordinal) {
                            (slideInHorizontally(animationSpec = tween(durationMillis = 300)) { fullWidth -> fullWidth } + fadeIn(
                                animationSpec = tween(durationMillis = 300)
                            )).togetherWith(
                                slideOutHorizontally(animationSpec = tween(durationMillis = 300)) { fullWidth -> -fullWidth } + fadeOut(
                                    animationSpec = tween(durationMillis = 300)
                                ))
                        } else {
                            (slideInHorizontally(animationSpec = tween(durationMillis = 300)) { fullWidth -> -fullWidth } + fadeIn(
                                animationSpec = tween(durationMillis = 300)
                            )).togetherWith(
                                slideOutHorizontally(animationSpec = tween(durationMillis = 300)) { fullWidth -> fullWidth } + fadeOut(
                                    animationSpec = tween(durationMillis = 300)
                                ))
                        }
                    },
                    label = "graphAnimation"
                ) { targetGraph ->
                    Column {
                        when (targetGraph) {
                            GraphType.COLUMN -> {
                                if (monthlyData.isNotEmpty()) {
                                    SavingGraph(monthlyData, zoomState = zoomState)
                                } else {
                                    Text(
                                        "Ingen data å vise enda.",
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        if (minPrice != null) {
                                            StatsCard(
                                                "Lavest besparelse i\n${MonthNames.getName(minPrice.month)}",
                                                "%.1f kr".format(minPrice.costEquivalent),
                                                infoTitle = "Hva betyr dette?",
                                                infoMessage = "Dette er måneden med lavest besparelse. Det vil si at i løpet av ett år, sparer du minst i denne måneden.",
                                                modifier = Modifier
                                            )
                                            Spacer(modifier = Modifier.width(16.dp))
                                        }
                                    }
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        if (maxPrice != null) {
                                            StatsCard(
                                                "Størst besparelse i\n${MonthNames.getName(maxPrice.month)}",
                                                "%.1f kr".format(maxPrice.costEquivalent),
                                                infoTitle = "Hva betyr dette?",
                                                infoMessage = "Dette er måneden med størst besparelse. Det vil si at i løpet av ett år, sparer du mest i denne måneden.",
                                                modifier = Modifier
                                            )
                                        }
                                    }
                                }
                            }

                            GraphType.LINE -> {
                                if (monthlyData.isNotEmpty()) {
                                    SavingLineGraph(monthlyData, zoomState = zoomState)
                                } else {
                                    Text(
                                        "Ingen data å vise enda.",
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        if (minProduction != null) {
                                            StatsCard(
                                                "Lavest produksjon i\n${
                                                    MonthNames.getName(
                                                        minProduction.month
                                                    )
                                                }",
                                                "%.1f kWh".format(minProduction.energyProduced),
                                                infoTitle = "Hva betyr dette?",
                                                infoMessage = "Dette er måneden med lavest produksjon. Det vil si at i løpet av ett år, produserer du minst i denne måneden.",
                                                modifier = Modifier
                                            )
                                            Spacer(modifier = Modifier.width(16.dp))
                                        }
                                    }
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        if (maxProduction != null) {
                                            StatsCard(
                                                "Størst produksjon i\n${
                                                    MonthNames.getName(
                                                        maxProduction.month
                                                    )
                                                }",
                                                "%.1f kWh".format(maxProduction.energyProduced),
                                                infoTitle = "Hva betyr dette?",
                                                infoMessage = "Dette er måneden med høyest produksjon. Det vil si at i løpet av ett år, produserer du mest i denne måneden.",
                                                modifier = Modifier
                                            )
                                        }
                                    }
                                }

                            }
                        }
                    }
                }

            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Detaljer per sesong",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (currentSeasonData != null) {
                            IconButton(
                                onClick = {
                                    if (seasonalProduction.isNotEmpty()) {
                                        currentSeasonIndex =
                                            (currentSeasonIndex - 1).coerceAtLeast(0)
                                    }
                                },
                                enabled = currentSeasonIndex > 0 && seasonalProduction.isNotEmpty()
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Forrige sesong",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                ProgressIndicator(
                                    percentage = currentSeasonData.percentageOfYearlyProduction.toFloat(),
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    "Produksjon",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    currentSeasonData.season.displayName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "%.1f kWh".format(currentSeasonData.totalProduction),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }


                            IconButton(
                                onClick = {
                                    if (seasonalProduction.isNotEmpty()) {
                                        currentSeasonIndex =
                                            (currentSeasonIndex + 1).coerceAtMost(seasonalProduction.lastIndex)
                                    }
                                },
                                enabled = currentSeasonIndex >= 0 && currentSeasonIndex < seasonalProduction.lastIndex
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Neste sesong",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            Text(
                                "Ingen sesongdata tilgjengelig.",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }


            item {
                Text(
                    "Detaljer per måned",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            val monthsToShow =
                if (currentSeasonData != null) {
                    monthlyData.filter { getSeasonForMonth(it.month) == currentSeasonData.season }
                } else {
                    monthlyData
                }

            items(monthsToShow) { month ->
                MonthlyDetailCard(month)
            }
        }
    }
}