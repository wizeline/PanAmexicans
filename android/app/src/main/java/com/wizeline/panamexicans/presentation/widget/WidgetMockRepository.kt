package com.wizeline.panamexicans.presentation.widget

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WidgetMockRepository {

    companion object {
        private var instance: WidgetMockRepository? = null
        fun getInstance(): WidgetMockRepository =
            if (instance == null) WidgetMockRepository() else instance!!
    }

    private val _widgetState: MutableStateFlow<PanAmexWidgetUiState> =
        MutableStateFlow(PanAmexWidgetUiState())
    val widgetState: StateFlow<PanAmexWidgetUiState> = _widgetState.asStateFlow()


    suspend fun getMiles() {
        for( i in 1..1000) {
            _widgetState.update { it.copy(miles = i) }
            delay(2000L)
        }
    }

}

