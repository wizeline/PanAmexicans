package com.wizeline.panamexicans.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wizeline.panamexicans.authentication.Authentication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(authentication: Authentication) : ViewModel() {
    private val _uiState = MutableStateFlow(SplashScreenUiState())
    val uiState: StateFlow<SplashScreenUiState> = _uiState.asStateFlow()
    private val _uiAction = MutableSharedFlow<UiAction>()
    val uiAction: SharedFlow<UiAction> get() = _uiAction

    init {
        viewModelScope.launch {
            delay(2000)
            _uiState.update { it.copy(showAppLogo = false) }
            if (authentication.getCurrentUser() != null) {
                _uiAction.emit(UiAction.NavigateToMain)
            } else {
                _uiAction.emit(UiAction.NavigateToLogin)
            }
        }
    }
}

data class SplashScreenUiState(
    val showAppLogo: Boolean = true
)

sealed class UiAction {
    data object NavigateToLogin : UiAction()
    data object NavigateToMain : UiAction()
}

sealed interface SplashScreenUiEvents {
    data object OnClick : SplashScreenUiEvents
}