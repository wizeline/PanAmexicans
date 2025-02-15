package com.wizeline.panamexicans.presentation.widget

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WidgetViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(WidgetUiState())
    val uiState: StateFlow<WidgetUiState> = _uiState.asStateFlow()
}

data class WidgetUiState(
    val isLoading: Boolean = false
)

sealed interface WidgetUiEvents {
    data object OnClick : WidgetUiEvents
}