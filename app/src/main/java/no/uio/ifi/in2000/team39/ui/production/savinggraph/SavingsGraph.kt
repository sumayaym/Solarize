package no.uio.ifi.in2000.team39.ui.production.savinggraph

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.VicoZoomState
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shader.verticalGradient
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import no.uio.ifi.in2000.team39.ui.production.model.MonthNames
import no.uio.ifi.in2000.team39.ui.production.model.MonthlyData
import java.text.DecimalFormat


private val BottomAxisLabelsKey = ExtraStore.Key<List<String>>()
private val StartAxisLabelSuffixKey = ExtraStore.Key<String>()

@Composable
fun ColumnChart(
    yValues: List<Double>,
    xLabels: List<String>? = null,
    yLabelSuffix: String? = null,
    showAxis: Boolean = true,
    columnThickness: Dp = 16.dp,
    columnSpacing: Dp = 2.dp,
    modifier: Modifier = Modifier,
    zoomState: VicoZoomState
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(yValues, xLabels, yLabelSuffix) {
        modelProducer.runTransaction {
            columnSeries {
                series(yValues)
            }
            extras {
                if (xLabels != null) {
                    it[BottomAxisLabelsKey] = xLabels
                }
                if (yLabelSuffix != null) {
                    it[StartAxisLabelSuffixKey] = yLabelSuffix
                }
            }
        }
    }

    val bottomAxisValueFormatter = remember {
        CartesianValueFormatter { context, x, _ ->
            context.model.extraStore[BottomAxisLabelsKey].getOrNull(x.toInt()) ?: "${x.toInt()}"
        }
    }

    val yDecimalFormat = remember {
        DecimalFormat("#.##${yLabelSuffix ?: ""}")
    }

    val startAxisValueFormatter = remember {
        CartesianValueFormatter { _, y, _ ->
            yDecimalFormat.format(y)
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(
                columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                    rememberLineComponent(
                        fill = fill(MaterialTheme.colorScheme.onPrimary),
                        thickness = columnThickness,
                        shape = CorneredShape.rounded(5)
                    )
                ),
                columnCollectionSpacing = columnSpacing
            ),
            startAxis = if (showAxis) {
                VerticalAxis.rememberStart(
                    valueFormatter = startAxisValueFormatter
                )
            } else null,
            bottomAxis = if (showAxis) {
                HorizontalAxis.rememberBottom(
                    itemPlacer = remember { HorizontalAxis.ItemPlacer.aligned() },
                    valueFormatter = bottomAxisValueFormatter
                )
            } else null,
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        zoomState = zoomState
    )
}

@Composable
fun SavingGraph(
    monthlyCostData: List<MonthlyData>,
    modifier: Modifier = Modifier,
    showAxis: Boolean = true,
    columnThickness: Dp = 16.dp,
    zoomState: VicoZoomState
) {
    val costs = monthlyCostData.map { it.costEquivalent ?: 0.0 }
    val months = monthlyCostData.map { MonthNames.getName(it.month).take(3) }

    ColumnChart(
        yValues = costs,
        xLabels = months,
        yLabelSuffix = " kr",
        showAxis = showAxis,
        modifier = modifier,
        columnThickness = columnThickness,
        zoomState = zoomState
    )
}

@Composable
fun LineChart(
    yValues: List<Double>,
    xLabels: List<String>? = null,
    yLabelSuffix: String? = null,
    showAxis: Boolean = true,
    modifier: Modifier = Modifier,
    pointSpacing: Dp = 16.dp,
    zoomState: VicoZoomState
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(yValues, xLabels, yLabelSuffix) {
        modelProducer.runTransaction {
            lineSeries {
                series(yValues)
            }
            extras {
                if (xLabels != null) {
                    it[BottomAxisLabelsKey] = xLabels
                }
                if (yLabelSuffix != null) {
                    it[StartAxisLabelSuffixKey] = yLabelSuffix
                }
            }
        }
    }

    val bottomAxisValueFormatter = remember {
        CartesianValueFormatter { context, x, _ ->
            context.model.extraStore[BottomAxisLabelsKey].getOrNull(x.toInt()) ?: "${x.toInt()}"
        }
    }

    val yDecimalFormat = remember {
        DecimalFormat("#.##${yLabelSuffix ?: ""}")
    }

    val startAxisValueFormatter = remember {
        CartesianValueFormatter { _, y, _ ->
            yDecimalFormat.format(y)
        }
    }

    val lineColor = MaterialTheme.colorScheme.onPrimary
    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.rememberLine(
                        fill = LineCartesianLayer.LineFill.single(fill(lineColor)),
                        areaFill =
                        LineCartesianLayer.AreaFill.single(
                            fill(
                                ShaderProvider.verticalGradient(
                                    arrayOf(lineColor.copy(alpha = 0.4f), Color.Transparent)
                                )
                            )

                        ),
                    )
                ),
                pointSpacing = pointSpacing
            ),
            startAxis = if (showAxis) {
                VerticalAxis.rememberStart(
                    valueFormatter = startAxisValueFormatter
                )
            } else null,
            bottomAxis = if (showAxis) {
                HorizontalAxis.rememberBottom(
                    itemPlacer = remember { HorizontalAxis.ItemPlacer.aligned() },
                    valueFormatter = bottomAxisValueFormatter
                )
            } else null,
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        zoomState = zoomState
    )
}

@Composable
fun SavingLineGraph(
    monthlyCostData: List<MonthlyData>,
    modifier: Modifier = Modifier,
    showAxis: Boolean = true,
    pointSpacing: Dp = 16.dp,
    zoomState: VicoZoomState
) {

    val costs = monthlyCostData.map { it.energyProduced }
    val months = monthlyCostData.map { MonthNames.getName(it.month).take(3) }

    LineChart(
        yValues = costs,
        xLabels = months,
        yLabelSuffix = " kwh",
        showAxis = showAxis,
        modifier = modifier,
        pointSpacing = pointSpacing,
        zoomState = zoomState
    )
}