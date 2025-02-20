package com.wizeline.panamexicans.presentation.widget

data class PanAmexWidgetUiState(
    val miles: Int? = null
)

sealed interface PanAmexWidgetState {
    data object Loading: PanAmexWidgetState
    data class OnRideMile(val miles: Int) : PanAmexWidgetState
}
