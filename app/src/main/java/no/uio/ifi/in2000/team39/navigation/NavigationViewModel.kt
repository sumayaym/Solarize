package no.uio.ifi.in2000.team39.navigation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor() : ViewModel() {
    private var _navBarSelectedIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    val navBarSelectedIndex: StateFlow<Int> = _navBarSelectedIndex.asStateFlow()

}
