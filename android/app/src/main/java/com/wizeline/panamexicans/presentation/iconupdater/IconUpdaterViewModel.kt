package com.wizeline.panamexicans.presentation.iconupdater

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class IconUpdaterViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(IconUpdaterUiState())
    val uiState: StateFlow<IconUpdaterUiState> = _uiState.asStateFlow()
}

data class IconUpdaterUiState(
    val isLoading: Boolean = false
)

sealed interface IconUpdaterUiEvents {
    data object OnClick : IconUpdaterUiEvents
}