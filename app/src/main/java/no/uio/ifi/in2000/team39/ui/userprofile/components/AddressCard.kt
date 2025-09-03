package no.uio.ifi.in2000.team39.ui.userprofile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team39.R
import no.uio.ifi.in2000.team39.model.databaseEntities.Home
import no.uio.ifi.in2000.team39.model.databaseEntities.RoofEntity

@Composable
fun AddressCard(home: Home, roof: RoofEntity, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = home.address,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Lengde: ${roof.roofLength} m", color = MaterialTheme.colorScheme.primary)
                    Text("Bredde: ${roof.roofWidth} m", color = MaterialTheme.colorScheme.primary)
                    Text("Vinkel: ${roof.roofAngle}°", color = MaterialTheme.colorScheme.primary)
                    Text(
                        "Retning: ${roof.roofDirection}",
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Antall paneler det er plass til: ${roof.panels}",
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }