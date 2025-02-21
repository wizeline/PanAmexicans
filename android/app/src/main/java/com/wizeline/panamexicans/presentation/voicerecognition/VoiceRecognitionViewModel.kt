package com.wizeline.panamexicans.presentation.voicerecognition

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wizeline.panamexicans.voice.VoiceInteractionService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class VoiceRecognitionViewModel @Inject constructor() : ViewModel() {
    val hasAudioPermission = MutableLiveData(false)
    val isOnline = MutableLiveData(true)

    private val _isSoftListening = MutableLiveData<Boolean>()
    val isSoftListening: LiveData<Boolean> = _isSoftListening

    private val _isActiveListening = MutableLiveData<Boolean>()
    val isActiveListening: LiveData<Boolean> = _isActiveListening

    fun updateAudioPermission(granted: Boolean) {
        hasAudioPermission.value = granted
    }

    fun updateOnlineStatus(online: Boolean) {
        isOnline.value = online
    }

    fun updateSoftListening(isSoftListening: Boolean) {
        _isSoftListening.value = isSoftListening
    }

    fun updateActiveListening(isActiveListening: Boolean) {
        _isActiveListening.value = isActiveListening
    }

    fun startVoiceService(context: Context) {
        val intent = Intent(context, VoiceInteractionService::class.java)
        _isSoftListening.value = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context, intent)
        } else {
            context.startService(intent)
        }
    }

    fun stopVoiceService(context: Context) {
        val intent = Intent(context, VoiceInteractionService::class.java)
        context.stopService(intent)
    }

    fun checkSoftListeningStatus(context: Context) {
        _isSoftListening.value = isVoiceServiceRunning(context)
    }

    private fun isVoiceServiceRunning(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (VoiceInteractionService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }
}