package com.wizeline.panamexicans.presentation.main.map

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    fun onEvent(event: MapUiEvents) {

    }
}

data class MapUiState(
    val isLoading: Boolean = false
)

sealed interface MapUiEvents {
    data object OnClick : MapUiEvents
}