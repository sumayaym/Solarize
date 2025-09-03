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
fun QandAPopup(onDismiss: () -> Unit) {

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
                    .background(
                        color = MaterialTheme.colorScheme.surface
                    )
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
                    text = "Ofte stilte spørsmål",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(20.dp))

                InfoSection(
                    title = "Hva skjer med overskuddsstrøm?",
                    items = listOf(
                        "Du kan selge overskuddsstrøm tilbake til strømnettet og få betalt fra strømleverandøren din.",
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
                    title = "Fungerer solceller på vinteren?",
                    items = listOf(
                        "Ja, men produksjonen er lavere. Solceller fungerer faktisk best i kaldt vær, men snø og mørketid reduserer produksjonen.",
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
                    title = "Hva skjer når det er overskyet?",
                    items = listOf(
                        "Solceller produserer fortsatt strøm, men mindre enn på solrike dager.",
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
                    title = "Hvor mye kan jeg spare?",
                    items = listOf(
                        "Det varierer, men mange sparer flere tusen kroner i året, spesielt med høye strømpriser.",
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
                    title = "Trenger jeg vedlikehold?",
                    items = listOf(
                        "Nei, solceller er nesten vedlikeholdsfrie. Du bør likevel fjerne snø og smuss for best effekt.",
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
                    title = "Hvor lenge varer solcellepaneler?",
                    items = listOf(
                        "De fleste har en levetid på 25–30 år og kommer med garanti på 20–25 år.",
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
                    title = "Må jeg ha batteri?",
                    items = listOf(
                        "Nei, men batterier kan lagre strøm til senere bruk og gjøre deg mindre avhengig av strømnettet.",
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
                    title = "Hva skjer når jeg ikke er hjemme?",
                    items = listOf(
                        "Strømmen du ikke bruker, sendes til nettet automatisk – du taper ikke produksjon."
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