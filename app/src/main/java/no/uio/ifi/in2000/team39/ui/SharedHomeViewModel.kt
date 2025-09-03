package no.uio.ifi.in2000.team39.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team39.data.settingsForDatabase.DatabaseRepository
import no.uio.ifi.in2000.team39.model.databaseEntities.Home
import no.uio.ifi.in2000.team39.model.databaseEntities.RoofEntity
import no.uio.ifi.in2000.team39.model.hks.PowerPriceArea
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SharedHomeViewModel @Inject constructor(
    private val homeRepo: DatabaseRepository
) : ViewModel() {

    private val _homes = MutableStateFlow<HomeUiState<List<Home>>>(HomeUiState.Idle)
    val homes: StateFlow<HomeUiState<List<Home>>> = _homes.asStateFlow()

    private val _addingHomeState = MutableStateFlow<AddingHomeUiState>(AddingHomeUiState.Idle)
    val addingHomeState: StateFlow<AddingHomeUiState> = _addingHomeState.asStateFlow()

    private val _selectedHome = MutableStateFlow<Home?>(null)
    val selectedHome: StateFlow<Home?> = _selectedHome.asStateFlow()

    private val _roofsForSelectedHome =
        MutableStateFlow<RoofUiState<List<RoofEntity>>>(RoofUiState.Idle)
    val roofsForSelectedHome: StateFlow<RoofUiState<List<RoofEntity>>> =
        _roofsForSelectedHome.asStateFlow()


    init {
        loadHomes()
    }

    fun loadRoofsForHome(homeId: Int) {
        _roofsForSelectedHome.update { RoofUiState.Loading }
        viewModelScope.launch {
            try {
                val loadedRoofs = homeRepo.getAllRoofSurfacesForHome(homeId)
                _roofsForSelectedHome.update { RoofUiState.Success(loadedRoofs) }
            } catch (e: Exception) {
                _roofsForSelectedHome.update {
                    RoofUiState.Error(
                        message = "Feil ved lasting av tak for bolig $homeId",
                        throwable = e
                    )
                }
            }
        }
    }

    fun loadHomes() {
        _homes.update { HomeUiState.Loading }
        viewModelScope.launch {
            try {
                val loadedHomes = homeRepo.getAllHomes()
                _homes.update { HomeUiState.Success(loadedHomes) }

                // Automatisk velg siste bolig hvis ingen er valgt
                if (_selectedHome.value == null && loadedHomes.isNotEmpty()) {
                    val lastHome = loadedHomes.lastOrNull()
                    _selectedHome.value = lastHome
                    _selectedHome.value?.let { loadRoofsForHome(it.id) } // Last tak for den første boligen ved oppstart
                } else if (_selectedHome.value != null) {
                    _selectedHome.value?.let { loadRoofsForHome(it.id) } // Oppdater taklisten hvis en bolig allerede er valgt ved lasting
                }
            } catch (e: Exception) {
                _homes.update {
                    HomeUiState.Error(
                        message = "Feil ved lasting av boliger",
                        throwable = e
                    )
                }
            }
        }
    }

    fun selectHome(home: Home) {
        _selectedHome.value = home
    }

    fun getPriceArea(address: String): String {
        val parts = address.split(",").map { it.trim() }
        if (parts.isNotEmpty()) {
            val county = parts.last()
            return PowerPriceArea.entries.find { it.counties.contains(county) }?.name ?: "NO1"
        }
        return PowerPriceArea.NO1.name
    }

    fun saveNewHomeWithRoof(
        address: String,
        latitude: Double,
        longitude: Double,
        length: Double,
        width: Double,
        angle: Double,
        direction: String
    ) {
        _addingHomeState.update { AddingHomeUiState.Loading }
        viewModelScope.launch {
            try {
                val existingHome = homeRepo.findHomeByAddress(address)

                if (existingHome != null) {
                    homeRepo.addRoofSurface(
                        homeId = existingHome.id,
                        length = length,
                        width = width,
                        direction = direction,
                        angle = angle,
                        year = (LocalDate.now().year - 1)
                    )
                    loadHomes()
                    _addingHomeState.update { AddingHomeUiState.Saved }
                } else {
                    val priceArea = getPriceArea(address)
                    homeRepo.createNewHomeWithRoof(
                        address = address,
                        latitude = latitude,
                        longitude = longitude,
                        length = length,
                        width = width,
                        angle = angle,
                        direction = direction,
                        priceArea = priceArea
                    )
                    loadHomes()
                    _addingHomeState.update { AddingHomeUiState.Saved }
                    val updatedHomes = homeRepo.getAllHomes()
                    _homes.update { HomeUiState.Success(updatedHomes) }

                    _selectedHome.value = updatedHomes.last()
                }

            } catch (e: Exception) {
                _homes.update {
                    HomeUiState.Error(
                        message = "Feil ved lagring av bolig",
                        throwable = e
                    )
                }
                _addingHomeState.update {
                    AddingHomeUiState.Error(
                        message = "Feil ved lagring av bolig",
                        throwable = e
                    )
                }
            }
        }
    }

    fun deleteHome(home: Home) {
        _homes.update { HomeUiState.Loading }
        viewModelScope.launch {
            try {
                homeRepo.deleteHome(home)

                val updatedHomes = homeRepo.getAllHomes()
                _homes.update { HomeUiState.Success(updatedHomes) }


                if (_selectedHome.value?.id == home.id) {
                    _selectedHome.value = updatedHomes.lastOrNull()
                }
            } catch (e: Exception) {
                _homes.update {
                    HomeUiState.Error(
                        message = "Feil ved sletting av bolig",
                        throwable = e
                    )
                }
            }
        }
    }

    fun resetState() {
        _addingHomeState.update { AddingHomeUiState.Idle }
    }

    fun clearAllAppData() {
        viewModelScope.launch {
            try {
                homeRepo.deleteAllData()
                _homes.value = HomeUiState.Success(emptyList())
                _roofsForSelectedHome.value = RoofUiState.Success(emptyList())
                _selectedHome.value = null
            } catch (e: Exception) {
                _homes.value = HomeUiState.Error("Feil ved sletting av alle data", e)
            }
        }
    }
}

sealed class HomeUiState<out T> {
    object Loading : HomeUiState<Nothing>()
    data class Success<out T>(val data: T) : HomeUiState<T>()
    data class Error(val message: String? = null, val throwable: Throwable? = null) :
        HomeUiState<Nothing>()

    object Idle : HomeUiState<Nothing>()
}

sealed class AddingHomeUiState {
    object Idle : AddingHomeUiState()
    object Loading : AddingHomeUiState()
    object Saved : AddingHomeUiState()
    data class Error(val message: String? = null, val throwable: Throwable? = null) :
        AddingHomeUiState()
}

sealed class RoofUiState<out T> {
    object Loading : RoofUiState<Nothing>()
    data class Success<out T>(val data: T) : RoofUiState<T>()
    data class Error(val message: String? = null, val throwable: Throwable? = null) :
        RoofUiState<Nothing>()

    object Idle : RoofUiState<Nothing>()
}