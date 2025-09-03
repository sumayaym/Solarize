package no.uio.ifi.in2000.team39.ui.userprofile.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team39.R

@Composable
fun AnimatedSun(
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0.4f) }

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 10000, easing = EaseOutCubic)
            )
        }
    }

    Image(
        painter = painterResource(id = R.drawable.sun_face),
        contentDescription = "Cat character",
        contentScale = ContentScale.Fit,
        modifier = modifier
            .scale(scale.value)
    )
}
