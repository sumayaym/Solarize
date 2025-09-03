package no.uio.ifi.in2000.team39.ui.production.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team39.ui.production.model.MonthNames
import no.uio.ifi.in2000.team39.ui.production.model.MonthlyData

// MonthlyDetailCard. Used to display the monthly detail card in the SavingsGraphScreen.

@Composable
fun MonthlyDetailCard(data: MonthlyData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(MonthNames.getName(data.month - 1), color = MaterialTheme.colorScheme.primary)

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${"%.1f".format(data.energyProduced)} kWh",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "${"%.2f".format(data.avgPrice)} kr/kWh",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "${"%.0f".format(data.costEquivalent)} kr spart",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}