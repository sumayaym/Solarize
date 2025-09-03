package no.uio.ifi.in2000.team39.ui.map.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// AddHouseButton is used in MapScreen, after address

@Composable
fun AddHouseButton(address: String?, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val isValidAddress = address?.matches(Regex(".*\\d+.*")) == true

    Button(
        onClick = onClick,
        enabled = isValidAddress,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isValidAddress) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.background.copy(
                alpha = 0.4f
            ),
            contentColor = if (isValidAddress) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background.copy(
                alpha = 0.6f
            ),
        )
    ) {
        Text("Legg til bolig", color = MaterialTheme.colorScheme.primary)
    }
}
