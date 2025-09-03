package no.uio.ifi.in2000.team39.ui.production.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// SmallStatCard. Used to display the small statistics in the SavingsGraphDetailScreen, and to display the statistics in the ProductionScreen.

@Composable
fun StatsCard(
    title: String,
    value: String,
    infoTitle: String,
    infoMessage: String,
    modifier: Modifier = Modifier,
    extraContent: @Composable (ColumnScope.() -> Unit)? = null
) {

    var showInfo by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .clickable { showInfo = true },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    value,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (extraContent != null) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    extraContent()
                }
            } else {
                Spacer(modifier = Modifier.width(0.dp))
            }
        }

    }
    if (showInfo) {
        AlertDialog(
            onDismissRequest = { showInfo = false },
            title = { Text(infoTitle, color = MaterialTheme.colorScheme.primary) },
            text = { Text(infoMessage, color = MaterialTheme.colorScheme.primary) },
            confirmButton = {
                TextButton(onClick = { showInfo = false }) {
                    Text("Skjønner", color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }
}