package com.wizeline.panamexicans.presentation.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WidgetViewModel @Inject constructor(
    private val widget: PanAmexWidgetUpdater,
    private val widgetRepository: WidgetMockRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(WidgetUiState())
    val uiState: StateFlow<WidgetUiState> = _uiState.asStateFlow()

    fun updateWidget() {
        viewModelScope.launch {
//            widget.update(
//                uiState =
//                    PanAmexWidgetUiState(miles = widgetRepository.getMiles())
//            )
            var miles = 0
            while (miles < 1000) {
                miles++
                widget.update(uiState = PanAmexWidgetUiState(miles = miles))
                delay(1000L)
            }

        }
    }

}

data class WidgetUiState(
    val isLoading: Boolean = false
)

sealed interface WidgetUiEvents {
    data object OnClick : WidgetUiEvents
}