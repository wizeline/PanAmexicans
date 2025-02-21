package com.wizeline.panamexicans.presentation.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wizeline.panamexicans.data.shareddata.SharedDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WidgetViewModel @Inject constructor(
    private val widget: PanAmexWidgetUpdater,
    private val sharedDataRepository: SharedDataRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(WidgetUiState())
    val uiState: StateFlow<WidgetUiState> = _uiState.asStateFlow()


    fun onEvent(event: WidgetUiEvents) {
        when (event) {
            is WidgetUiEvents.OnClickResetMiles -> {
                clearWidget()
            }
        }
    }

    private fun clearWidget() {
        viewModelScope.launch {
            sharedDataRepository.clearMiles()
            widget.update(
                uiState = PanAmexWidgetUiState(
                    miles = sharedDataRepository.getMilesCounter().toInt()
                )
            )
        }
    }

}

data class WidgetUiState(
    val isLoading: Boolean = false
)

sealed interface WidgetUiEvents {
    data object OnClickResetMiles : WidgetUiEvents
}