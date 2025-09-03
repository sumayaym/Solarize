package no.uio.ifi.in2000.team39.ui.userprofile.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun SettingsPopup(
    onDismiss: () -> Unit,
    onResetAppData: () -> Unit
) {
    var showPrivacyDialog by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
            Box {
                // Tilbake‐pil
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Tilbake",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 72.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    Text(
                        text = "Innstillinger",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary

                    )
                    Spacer(Modifier.height(16.dp))


                    HorizontalDivider(
                        thickness = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Spacer(Modifier.height(32.dp))

                    // 1) App-versjon
                    SettingItem(label = "App-versjon", content = {
                        Text("1.0.0", color = MaterialTheme.colorScheme.primary)
                    })
                    HorizontalDivider(
                        thickness = 2.dp,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    // 2) Personvernpolicy (åpner egen popup)
                    SettingItem(label = "Personvernpolicy", content = {
                        TextButton(onClick = { showPrivacyDialog = true }) {
                            Text("Les", color = MaterialTheme.colorScheme.primary)
                        }
                    })
                    HorizontalDivider(
                        thickness = 2.dp,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    // 3) Tilbakestill appdata
                    SettingItem(label = "Tilbakestill appdata", content = {
                        TextButton(
                            onClick = onResetAppData,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Slett alle data")
                        }
                    })

                    Spacer(Modifier.padding(32.dp))


                }
            }
        }
    }

    // Personvern-popup
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Personvernpolicy", color = MaterialTheme.colorScheme.primary) },
            text = {
                Column(
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxHeight(0.6f)) {
                    Text(
                        "Denne appen samler ikke inn, lagrer eller deler noen form for personlig informasjon med tredjepart.\n" +
                                "\n" +
                                "All data du legger inn i appen (som adresse, takvinkel og areal) lagres kun lokalt på enheten din. Dette betyr at ingen av dine data blir sendt til, eller lagret på, eksterne servere.\n" +
                                "\n" +
                                "Du har når som helst mulighet til å slette alle lagrede data via menyvalget \"Slett alle data\" i innstillinger.\n" +
                                "\n" +
                                "Ingen av funksjonene i appen krever tilgang til sensitive tillatelser som kamera, kontakter eller posisjon.\n" +
                                "\n" +
                                "Ved å bruke denne appen samtykker du til denne praksisen.\n",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Lukk", color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }
}

@Composable
private fun SettingItem(
    label: String,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.primary
        )
        content()
    }
}
