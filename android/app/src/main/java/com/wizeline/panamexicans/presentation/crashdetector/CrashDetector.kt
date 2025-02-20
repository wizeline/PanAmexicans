package com.wizeline.panamexicans.presentation.crashdetector

import kotlinx.coroutines.flow.StateFlow

data class CrashState(
    val isFalling: Boolean = false,
    val isCrashRisk: Boolean = false,
    val fallTimeStamps: List<String> = emptyList()
)

interface CrashDetector {
    val crashState: StateFlow<CrashState>

    fun startListening() {}
    fun stopListening() {}
}
