package no.uio.ifi.in2000.team39.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = White,
    secondary = OrangeSecondaryDark,
    tertiary = OrangeTertiaryDark,
    background = Black,
    surface = SurfaceDark,
    onPrimary = OrangePrimaryDark,
    onBackground = LightBeige,
    outline = Beige
)

private val LightColorScheme = lightColorScheme(
    primary = Black,
    secondary = OrangeSecondary,
    tertiary = OrangeTertiary,
    background = White,
    surface = SurfaceLight,
    onPrimary = OrangePrimary, //brukes til loading ikon go dividers
    onBackground = DarkBrown, //brukes til Overskrifter
    outline = Brown //brukes til kanter på bokser

)


/* Other default colors to override
background = Color(0xFFFFFBFE),
surface = Color(0xFFFFFBFE),
onPrimary = Color.White,
onSecondary = Color.White,
onTertiary = Color.White,
onBackground = Color(0xFF1C1B1F),
onSurface = Color(0xFF1C1B1F),
*/


@Composable
fun Team39Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}