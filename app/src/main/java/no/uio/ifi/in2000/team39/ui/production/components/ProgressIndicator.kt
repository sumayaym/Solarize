package no.uio.ifi.in2000.team39.ui.production.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team39.ui.theme.OrangePrimary

// ProgressIndicator. Used to display the progress indicator in the ProductionScreen and SavingsGraphDetailScreen.

@Composable
fun ProgressIndicator(
    percentage: Float,
    modifier: Modifier = Modifier,
    indicatorColor: Color = OrangePrimary,
    trackColor: Color = Color.LightGray,
    strokeWidth: Float = 35f
) {
    val animatedProgress = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(percentage) {
        coroutineScope.launch {
            animatedProgress.animateTo(
                targetValue = percentage,
                animationSpec = tween(durationMillis = 600)
            )
        }
    }

    Box(
        modifier = modifier
            .size(200.dp)
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val currentPercentage = animatedProgress.value
            val sweepAngle = (currentPercentage / 100) * 240f
            val center = Offset(size.width / 2, size.height / 2)
            val radius = (size.minDimension - strokeWidth) / 2

            drawArc(
                color = trackColor,
                startAngle = -210f,
                sweepAngle = 240f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth)
            )

            drawArc(
                color = indicatorColor,
                startAngle = -210f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Text(
            text = "${animatedProgress.value.toInt()}%",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}