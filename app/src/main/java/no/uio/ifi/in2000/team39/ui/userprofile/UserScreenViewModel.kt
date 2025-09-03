package no.uio.ifi.in2000.team39.ui.userprofile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class UserScreenViewModel @Inject constructor() : ViewModel() {

    private val _isInfoPopupVisible = MutableStateFlow(false)
    val isInfoPopupVisible: StateFlow<Boolean> = _isInfoPopupVisible.asStateFlow()

    private val _isQandAPopupVisible = MutableStateFlow(false)
    val isQandAPopupVisible: StateFlow<Boolean> = _isQandAPopupVisible.asStateFlow()


    fun toggleInfoPopup() {
        _isInfoPopupVisible.value = !_isInfoPopupVisible.value
    }

    fun toggleQandAPopup() {
        _isQandAPopupVisible.value = !_isQandAPopupVisible.value
    }

}
