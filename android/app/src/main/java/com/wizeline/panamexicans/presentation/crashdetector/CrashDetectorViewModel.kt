package com.wizeline.panamexicans.presentation.crashdetector

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CrashDetectorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CrashDetectorUiState())
    val uiState: StateFlow<CrashDetectorUiState> = _uiState.asStateFlow()
}

data class CrashDetectorUiState(
    val isLoading: Boolean = false
)

sealed interface CrashDetectorUiEvents {
    data object OnClick : CrashDetectorUiEvents
}