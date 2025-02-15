package com.wizeline.panamexicans.presentation.ridesessions

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RideSessionsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RideSessionsUiState())
    val uiState: StateFlow<RideSessionsUiState> = _uiState.asStateFlow()
}

data class RideSessionsUiState(
    val isLoading: Boolean = false
)

sealed interface RideSessionsUiEvents {
    data object OnClick : RideSessionsUiEvents
}