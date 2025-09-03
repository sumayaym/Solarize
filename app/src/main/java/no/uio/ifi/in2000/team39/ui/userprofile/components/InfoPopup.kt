package no.uio.ifi.in2000.team39.ui.userprofile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun InfoPopup(onDismiss: () -> Unit) {

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .background(color = MaterialTheme.colorScheme.surface)
                    .padding(20.dp)
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Lukk informasjon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "Lær mer om solcellepaneler",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(20.dp))

                InfoSection(
                    title = "Fordeler i Norge",
                    items = listOf(
                        "Kaldt klima vil si høyere effektivitet. Solcellepaneler yter faktisk bedre når de er kalde. -5°C er nærmest ideelt.",
                        "Lange, lyse sommerdager gir høy produksjon."
                    )
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                InfoSection(
                    title = "Økonomi og støtteordninger",
                    items = listOf(
                        "Enova gir støtte opptil 32.500 kr for å produsere din egen strøm.",
                        "Gjelder både helårsboliger og fritidsboliger.",
                        "Ekstra støtte mulig for batteri og energilagring, dersom du også ønsker å lagre strømmen du produserer.",
                        "Les mer på enova.no for oppdatert informasjon."
                    )
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                InfoSection(
                    title = "Kostnad og lønnsomhet",
                    items = listOf(
                        "Installering koster typisk mellom 60.000–140.000 kr.",
                        "Lønnsomhet avhenger av strømpris, panelareal og innstråling.",
                        "Virkningsgraden til en solcelle angir hvor effektivt den konverterer sollys til elektrisitet. Virkningsgrad for paneler ligger ofte mellom 18–22 %."
                    )
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                InfoSection(
                    title = "Hva appen hjelper deg med",
                    items = listOf(
                        "Estimerer produksjon (kWh) basert på takdata og geografisk plassering.",
                        "Henter værdata fra Frost og PVGIS for nøyaktighet.",
                        "Beregner besparelse sammenlignet med strømprisene"
                    )
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                InfoSection(
                    title = "Forskjellige typene solceller",
                    items = listOf(
                        "Monokrystallinske paneler: De er mørke i fargen, krever lite plass og passer godt på små tak med høyt energibehov.",
                        "Tynnfilmssolceller (amorfe): Veldig fleksible og lette. Lavere effektivitet, men kan være nyttige på buede eller spesielle overflater. ",
                        "Polykrystallinske solceller: Litt lavere effekt, men ofte billigere. De har en blålig farge og passer best der det er god plass på taket.",
                    )
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )


                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                InfoSection(
                    title = "Navigering i appen",
                    items = listOf(
                        "Legg til bolig fra kartskjermen: Søk på ønsket adresse og fyll inn takets lengde, bredde, vinkel og retning.",
                        "Hovedsiden viser estimert strømproduksjon og besparelse basert bolig og værdata. Trykk på grafen for detaljerte analyser måned for måned.",
                        "På profilsiden finner du informasjon om valgt bolig, med mulighet for å bytte eller slette bolig.",
                        "Gå til Innstillinger for å lese personvernpolicy eller tilbakestille appdata."
                    )
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )

            }
        }
    }
}