package no.uio.ifi.in2000.team39.ui.userprofile.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team39.R


@Composable
fun SettingsIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(32.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.settings_icon),
            contentDescription = "Innstillinger",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}
