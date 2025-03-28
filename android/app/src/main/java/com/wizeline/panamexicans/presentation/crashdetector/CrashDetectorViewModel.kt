package com.wizeline.panamexicans.presentation.crashdetector

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


const val RESET_TIMER = 10

@HiltViewModel
class CrashDetectorViewModel @Inject constructor(
    private val crashDetector: CrashDetector
) : ViewModel() {
    private val _uiState = MutableStateFlow(CrashDetectorUiState())
    val uiState: StateFlow<CrashDetectorUiState> = _uiState.asStateFlow()

    init {
        collectCrashDetection()
        crashDetector.startListening()
    }

    fun onEvent(event: CrashDetectorUiEvents) {
        when (event) {
            is CrashDetectorUiEvents.OnCrashDetected -> {
                //Todo: Emergency event ex: Inform riders, call 911, etc
            }

            else -> Unit
        }
    }

    private fun hideEmergencyTrigger() {
        viewModelScope.launch {
            for (i in RESET_TIMER downTo 1) {
                delay(1000L)
                _uiState.update { it.copy(timer = i) }
            }
            _uiState.update {
                it.copy(
                    isCrashDetected = false,
                    timer = RESET_TIMER
                )
            }
        }
    }

    private fun collectCrashDetection() {
        viewModelScope.launch {
            crashDetector.crashState.collect { state ->
                state.apply {
                    _uiState.update {
                        it.copy(
                            isCrashDetected = isCrashRisk,
                            fallTimeStamps = fallTimeStamps.toString()
                        )
                    }
                    if (isCrashRisk) {
                        Log.d("CrashDetector", "Detected crash: $isCrashRisk")
                        hideEmergencyTrigger()
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        crashDetector.stopListening()
    }
}

data class CrashDetectorUiState(
    val isLoading: Boolean = false,
    val isCrashDetected: Boolean = false,
    val fallTimeStamps: String = "",
    val timer: Int = RESET_TIMER
)

sealed interface CrashDetectorUiEvents {
    data object OnCrashDetected : CrashDetectorUiEvents
}