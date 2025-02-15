package com.wizeline.panamexicans.presentation.routecalculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RouteCalculatorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RouteCalculatorUiState())
    val uiState: StateFlow<RouteCalculatorUiState> = _uiState.asStateFlow()
}

data class RouteCalculatorUiState(
    val isLoading: Boolean = false
)

sealed interface RouteCalculatorUiEvents {
    data object OnClick : RouteCalculatorUiEvents
}