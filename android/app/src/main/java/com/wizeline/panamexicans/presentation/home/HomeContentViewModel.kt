package com.wizeline.panamexicans.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wizeline.panamexicans.authentication.Authentication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeContentViewModel @Inject constructor(val authentication: Authentication) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeContentUiState())
    val uiState: StateFlow<HomeContentUiState> = _uiState.asStateFlow()
    private val _uiAction = MutableSharedFlow<HomeContentUiAction>()
    val uiAction: SharedFlow<HomeContentUiAction> = _uiAction.asSharedFlow()

    fun onEvent(event: HomeContentUiEvents) {
        when (event) {
            is HomeContentUiEvents.OnLogoutClicked -> {
                authentication.logout()
                viewModelScope.launch {
                    _uiAction.emit(HomeContentUiAction.OnAccountLogged)
                }
            }

            else -> Unit
        }
    }
}

data class HomeContentUiState(
    val isLoading: Boolean = false
)

sealed class HomeContentUiAction {
    data object OnAccountLogged : HomeContentUiAction()
}

sealed interface HomeContentUiEvents {
    data object OnWidgetClicked : HomeContentUiEvents
    data object OnCrashDetectorClicked : HomeContentUiEvents
    data object OnLogoutClicked : HomeContentUiEvents
    data object OnIconUpdaterClicked : HomeContentUiEvents
    data object OnRideSessionsClicked : HomeContentUiEvents
    data object OnFunctionalPocClicked : HomeContentUiEvents
    data object OnRouteCalculatorClicked : HomeContentUiEvents
    data object OnVoiceRecognitionClicked : HomeContentUiEvents
}