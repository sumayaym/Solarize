package no.uio.ifi.in2000.team39.ui.production

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import no.uio.ifi.in2000.team39.R
import no.uio.ifi.in2000.team39.ui.theme.OrangePrimary
import no.uio.ifi.in2000.team39.ui.util.formatNumber
import kotlin.math.pow

@Composable
fun ProfitabilityScreen(
    viewModel: ProdscreenViewModel = hiltViewModel(),
    navController: NavController
) {
    val monthlyData by viewModel.monthlyProductionData.collectAsState()
    val investment by viewModel.investment.collectAsState()

    val yearlySavings = monthlyData.sumOf { it.costEquivalent ?: 0.0 }
    val yearlyProduction = monthlyData.sumOf { it.energyProduced }

    val enovaSupport = 32500.0

    val paybackYearsWithoutSupport =
        if (yearlySavings != 0.0) investment.toDouble() / yearlySavings else 0.0
    val paybackYearsWithSupport =
        if (yearlySavings != 0.0) (investment.toDouble() - enovaSupport) / yearlySavings else 0.0

    val inflationRate = 0.026
    val totalSavings = (0 until 30).fold(0.0) { acc, year ->
        acc + yearlySavings * (1 + inflationRate).pow(year.toDouble())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.tertiary)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Tilbake",
                tint = MaterialTheme.colorScheme.outline
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Potensiell besparelse med solceller",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            "Basert på din plassering og takforhold",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ResultRow(
                    painterResource(id = R.drawable.sun_face),
                    "Forventet årlig produksjon",
                    "${formatNumber(yearlyProduction.toInt())} kWh/år",
                    "Basert på din lokasjon"
                )
                ResultRow(
                    painterResource(id = R.drawable.money),
                    "Årlig besparelse",
                    "${formatNumber(yearlySavings.toInt())} kr",
                    "Din reduserte strømregning"
                )
                ResultRow(
                    painterResource(id = R.drawable.home),
                    "Tilbakebetalingstid (uten Enova)",
                    "%.1f år".format(paybackYearsWithoutSupport),
                    "Basert på investeringskostnad"
                )
                ResultRow(
                    painterResource(id = R.drawable.home),
                    "Tilbakebetalingstid (med Enova)",
                    "%.1f år".format(paybackYearsWithSupport),
                    "Etter estimert støtte på ${formatNumber(enovaSupport.toInt())} kr"
                )
                ResultRow(
                    painterResource(id = R.drawable.pil),
                    "Besparelse over 30 år",
                    "${formatNumber(totalSavings.toInt())} kr",
                    "Forventet levetid for solcelleanlegget"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ResultRow(
    iconRes: Painter,
    title: String,
    value: String,
    subtitle: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = iconRes,
            contentDescription = title,
            modifier = Modifier
                .size(32.dp)
                .padding(end = 12.dp),
            tint = OrangePrimary
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}