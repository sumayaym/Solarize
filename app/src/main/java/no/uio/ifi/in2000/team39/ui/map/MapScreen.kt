package no.uio.ifi.in2000.team39.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team39.ui.AddingHomeUiState
import no.uio.ifi.in2000.team39.ui.SharedHomeViewModel
import no.uio.ifi.in2000.team39.ui.components.LoadingScreen
import no.uio.ifi.in2000.team39.ui.map.components.AddHouseButton
import no.uio.ifi.in2000.team39.ui.map.components.AddressBottomSheet
import no.uio.ifi.in2000.team39.ui.map.components.LayerToggleButton
import no.uio.ifi.in2000.team39.ui.map.components.MapViewComponent
import no.uio.ifi.in2000.team39.ui.map.components.SearchBar
import no.uio.ifi.in2000.team39.ui.map.components.SuggestionsDropdown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    mapViewModel: MapViewModel,
    sharedHomeViewModel: SharedHomeViewModel
) {

    // This is the map screen

    val mapUiState by mapViewModel.uiState.collectAsState()
    val suggestions by mapViewModel.suggestions.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var searchQuery by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    var showBottomSheet by remember { mutableStateOf(false) }
    var showSuggestions by remember { mutableStateOf(false) }

    val addingHomeState by sharedHomeViewModel.addingHomeState.collectAsState()
    var isSavingHome by remember { mutableStateOf(false) }

    LaunchedEffect(mapUiState.address) {
        mapUiState.address?.let { newAddress ->
            searchQuery = newAddress
        } ?: run {
            searchQuery = ""
        }
    }

    val snackbarMessage by mapViewModel.snackbarMessage.collectAsState()
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            mapViewModel.clearSnackbarMessage()
        }
    }

    LaunchedEffect(addingHomeState) {
        when (addingHomeState) {
            AddingHomeUiState.Loading -> {
                isSavingHome = true
            }

            AddingHomeUiState.Saved -> {
                isSavingHome = false
                snackbarHostState.showSnackbar("Bolig lagret!")
                sharedHomeViewModel.loadHomes()
                sharedHomeViewModel.resetState()
            }

            is AddingHomeUiState.Error -> {
                isSavingHome = false
                val errorMessage = (addingHomeState as AddingHomeUiState.Error).message
                    ?: "Feil ved lagring av bolig"
                snackbarHostState.showSnackbar(errorMessage)
                sharedHomeViewModel.resetState()
            }

            AddingHomeUiState.Idle -> {
                isSavingHome = false
            }
        }
    }

    if (isSavingHome) {
        LoadingScreen()
        return
    }

    // Main Map and UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        // Map Component
        MapViewComponent(
            mapViewModel = mapViewModel,
            onMapClick = {
                keyboardController?.hide()
                showSuggestions = false
            },
            onMapLongClick = { latLng ->
                mapViewModel.reverseGeocode(latLng.latitude, latLng.longitude)
            }
        )

        // Search and Suggestions
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp)
                .padding(top = 50.dp)
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = {
                    searchQuery = it
                    mapViewModel.fetchSuggestions(it)
                    showSuggestions = true
                },
                onSearch = {
                    if (searchQuery.isNotBlank()) {
                        mapViewModel.searchAddress(searchQuery)
                        keyboardController?.hide()
                        showSuggestions = false
                    }
                },
                searchState = mapUiState.searchState,
                displayName = mapUiState.address
            )

            // Suggestions Dropdown
            if (showSuggestions && suggestions.isNotEmpty()) {
                SuggestionsDropdown(
                    suggestions = suggestions,
                    onSuggestionClick = { suggestion ->
                        searchQuery = suggestion
                        mapViewModel.searchAddress(suggestion)
                        showSuggestions = false
                        keyboardController?.hide()
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Add House Button
            AddHouseButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                address = mapUiState.address,
                onClick = {
                    if (mapUiState.address.isNullOrBlank()) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Mangler gyldig adresse")
                        }
                    } else {
                        showBottomSheet = true
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Layer Toggle Button
            LayerToggleButton(
                onClick = {
                    mapViewModel.toggleLayer()
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .size(48.dp)
            )
        }

        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Address Bottom Sheet
        if (showBottomSheet) {
            ModalBottomSheet(onDismissRequest = { showBottomSheet = false }) {
                AddressBottomSheet(
                    geocodingResult = mapUiState.geocodingResult,
                    onSave = { fullAddress, length, width, angle, direction ->
                        mapUiState.coordinates?.let { coordinates ->
                            sharedHomeViewModel.saveNewHomeWithRoof(
                                address = fullAddress,
                                latitude = coordinates.latitude,
                                longitude = coordinates.longitude,
                                length = length.toDouble(),
                                width = width.toDouble(),
                                angle = angle.toDouble(),
                                direction = direction
                            )
                            showBottomSheet = false
                        }
                    },
                    onDismiss = { showBottomSheet = false }
                )
            }
        }
    }
}