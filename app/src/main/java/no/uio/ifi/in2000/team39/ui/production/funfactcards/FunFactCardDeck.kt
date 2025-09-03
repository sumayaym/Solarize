package no.uio.ifi.in2000.team39.ui.production.funfactcards

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team39.ui.production.model.MonthlyData
import kotlin.math.floor

// FunFactCardDeck. Used to display the fun facts in the ProductionScreen.

data class FunFact(val title: String, val fact: String)

@Composable
fun FunFactCardDeck(monthlyData: MonthlyData, modifier: Modifier) {

    val funFacts = listOf(
        FunFact(
            title = "Kilometer kjørt med elbil",
            fact = "Med produsert energi for denne måneden kan du kjøre ca. ${floor(monthlyData.energyProduced / 0.2).toInt()} km med elbil."
        ),
        FunFact(
            title = "Antall vask",
            fact = "Du har produsert nok energi til ca. ${floor(monthlyData.energyProduced / 0.5).toInt()} vask med vaskemaskin for denne måneden.",
        ),
        FunFact(
            title = "Dager med TV",
            fact = "Du kan se på TV i ca. ${floor(monthlyData.energyProduced / (0.1 * 24)).toInt()} dager med den produserte energien for denne måneden.",
        )
    )


    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)

    ) {
        Spacer(modifier = Modifier.width(1.dp))
        funFacts.forEach { funFact ->
            FunFactCard(
                title = funFact.title,
                fact = funFact.fact
            )
        }
        Spacer(modifier = Modifier.width(1.dp))
    }
}