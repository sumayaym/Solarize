package no.uio.ifi.in2000.team39.ui.userprofile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import no.uio.ifi.in2000.team39.R
import no.uio.ifi.in2000.team39.model.databaseEntities.Home
import no.uio.ifi.in2000.team39.model.databaseEntities.RoofEntity
import no.uio.ifi.in2000.team39.ui.HomeUiState
import no.uio.ifi.in2000.team39.ui.RoofUiState
import no.uio.ifi.in2000.team39.ui.SharedHomeViewModel
import no.uio.ifi.in2000.team39.ui.components.HomeSelectorBarDropdown
import no.uio.ifi.in2000.team39.ui.userprofile.components.AddressCard
import no.uio.ifi.in2000.team39.ui.userprofile.components.AnimatedSun
import no.uio.ifi.in2000.team39.ui.userprofile.components.InfoBox
import no.uio.ifi.in2000.team39.ui.userprofile.components.InfoPopup
import no.uio.ifi.in2000.team39.ui.userprofile.components.QandABox
import no.uio.ifi.in2000.team39.ui.userprofile.components.QandAPopup
import no.uio.ifi.in2000.team39.ui.userprofile.components.SettingsIcon
import no.uio.ifi.in2000.team39.ui.userprofile.components.SettingsPopup

@Composable
fun UserProfileScreen(
    sharedHomeViewModel: SharedHomeViewModel,
    userViewModel: UserScreenViewModel = hiltViewModel()
) {
    val homesState by sharedHomeViewModel.homes.collectAsState()
    val roofsState by sharedHomeViewModel.roofsForSelectedHome.collectAsState()
    val selectedHome by sharedHomeViewModel.selectedHome.collectAsState()
    val showInfoPopup by userViewModel.isInfoPopupVisible.collectAsState()
    val showQandAPopup by userViewModel.isQandAPopupVisible.collectAsState()
    var showBubble by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var homes by remember { mutableStateOf<List<Home>>(emptyList()) }
    var showConfirmResetDialog by remember { mutableStateOf(false) }
    var currentRoofIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        sharedHomeViewModel.loadHomes()
    }

    LaunchedEffect(selectedHome) {
        selectedHome?.id?.let { sharedHomeViewModel.loadRoofsForHome(it) }
        currentRoofIndex = 0
    }

    LaunchedEffect(homesState) {
        when (homesState) {
            is HomeUiState.Success -> {
                homes = (homesState as HomeUiState.Success<List<Home>>).data
            }

            is HomeUiState.Error -> {
                // Not used now, maybe in the future
            }

            HomeUiState.Loading -> {
                // Not used now, maybe in the future
            }

            HomeUiState.Idle -> {
                // Not used now, maybe in the future
            }
        }
    }

    val homeToShow = selectedHome ?: Home(
        address = "Ingen bolig valgt",
        latitude = 0.0,
        longitude = 0.0,
        priceArea = "NO1"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 50.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HomeSelectorBarDropdown(
                    selectedHome = selectedHome,
                    homes = homes,
                    onHomeSelected = {
                        sharedHomeViewModel.selectHome(it)
                        showBubble = false
                    },
                    onDeleteHome = { sharedHomeViewModel.deleteHome(it) },
                    showSnackBar = {
                        showBubble = true
                    },
                    modifier = Modifier.weight(0.8f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                SettingsIcon(
                    onClick = { showSettingsDialog = true }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.solarpanel),
                    contentDescription = "Solcellepanel bilde",
                    contentScale = ContentScale.Crop,
                )

                BoxWithConstraints(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val rowWidth = maxWidth
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        if (showBubble) {
                            Image(
                                painter = painterResource(R.drawable.speech_bubble),
                                contentDescription = null,
                                modifier = Modifier
                                    .width(rowWidth * 0.6f)
                                    .aspectRatio(1.6f)
                                    .offset(y = (-30).dp)
                            )
                        }

                        AnimatedSun(
                            modifier = Modifier
                                .size(rowWidth * 0.4f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (roofsState) {
                is RoofUiState.Success -> {
                    val roofs = (roofsState as RoofUiState.Success<List<RoofEntity>>).data
                    if (roofs.isNotEmpty()) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)

                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(
                                    onClick = { if (currentRoofIndex > 0) currentRoofIndex-- },
                                    enabled = currentRoofIndex > 0
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Forrige sesong",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AddressCard(
                                        home = homeToShow,
                                        roof = roofs[currentRoofIndex],
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                IconButton(
                                    onClick = { if (currentRoofIndex < roofs.size - 1) currentRoofIndex++ },
                                    enabled = currentRoofIndex < roofs.size - 1
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Neste sesong",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            AddressCard(
                                home = homeToShow,
                                roof = RoofEntity(0, 0, 1, 0.0, 0.0, "", 0.0, 0)
                            )
                        }
                    }
                }

                is RoofUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                is RoofUiState.Error -> {
                    Text("Feil ved lasting av tak.", color = MaterialTheme.colorScheme.error)
                }

                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        AddressCard(
                            home = homeToShow,
                            roof = RoofEntity(0, 0, 1, 0.0, 0.0, "", 0.0, 0)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)

            ) {
                InfoBox(
                    onClick = { userViewModel.toggleInfoPopup() },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                QandABox(
                    onClick = { userViewModel.toggleQandAPopup() },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            if (showInfoPopup) {
                InfoPopup(onDismiss = { userViewModel.toggleInfoPopup() })
            }
            if (showQandAPopup) {
                QandAPopup(onDismiss = { userViewModel.toggleQandAPopup() })
            }

            if (showSettingsDialog) {
                SettingsPopup(
                    onDismiss = { showSettingsDialog = false },
                    onResetAppData = {
                        showConfirmResetDialog = true
                    }
                )
            }

            if (showConfirmResetDialog) {
                AlertDialog(
                    onDismissRequest = { showConfirmResetDialog = false },
                    title = { Text("Slett alle data?", color = MaterialTheme.colorScheme.primary) },
                    text = {
                        Text(
                            "Er du sikker på at du vil slette alle lagrede data? Dette kan ikke angres.",
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            sharedHomeViewModel.clearAllAppData()
                            showConfirmResetDialog = false
                            showSettingsDialog = false
                        }) {
                            Text("Slett", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showConfirmResetDialog = false
                        }) {
                            Text("Avbryt", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}