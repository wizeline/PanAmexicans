package com.wizeline.panamexicans.voice

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.wizeline.panamexicans.voice.Constants.ACTIVE_LISTENING_ON
import com.wizeline.panamexicans.voice.Constants.ACTIVE_WORD
import com.wizeline.panamexicans.voice.Constants.DEACTIVATED_WORD
import com.wizeline.panamexicans.voice.Constants.STOP_VOICE_SERVICE
import com.wizeline.panamexicans.voice.Constants.TTS_RESPONSE

class VoiceInteractionService : Service() {
    private var speechRecognizer: SpeechRecognizer? = null
    private lateinit var recognizerIntent: Intent

    override fun onCreate() {
        super.onCreate()
        Log.d("VoiceService", "Service ready")
        createNotificationChannel()
        startForeground(1, createNotification())

        initSpeechRecognizer()
        startListeningForWord()
    }

    private fun initSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            if (!isOnline()) {
                putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
            }
        }

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                var stopSoftListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.let { match ->
                    if(match.isNotEmpty() && match[0].equals(ACTIVE_WORD, ignoreCase = true)) { //hey harley
                        Log.d("VoiceService", "Service activated")
                        stopSoftListening = true
                        actionOnWakeWord()
                    } else if (match.isNotEmpty() && match[0].equals(DEACTIVATED_WORD, ignoreCase = true)) {
                        Toast.makeText(baseContext, "Service deactivated", Toast.LENGTH_SHORT).show()
                        stopSoftListening = true
                        stopSelf()

                        val intent = Intent(STOP_VOICE_SERVICE)
                        sendBroadcast(intent)
                    }
                }
                if (!stopSoftListening) {
                    startListeningForWord()
                }
            }

            override fun onError(error: Int) {
                if (error == SpeechRecognizer.ERROR_SERVER) {
                    Log.e("VoiceService", "Voice service error: $error")
                    speechRecognizer?.destroy()
                    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this@VoiceInteractionService)
                    initSpeechRecognizer()
                } else {
                    Log.e("VoiceService", "Service error: $error")
                }
                startListeningForWord()
            }

            override fun onReadyForSpeech(params: Bundle?) {
                //Nothing to add
            }
            override fun onBeginningOfSpeech() {
                //Nothing to add
            }
            override fun onRmsChanged(rmsdB: Float) {
                //Nothing to add
            }
            override fun onBufferReceived(buffer: ByteArray?) {
                //Nothing to add
            }
            override fun onEndOfSpeech() {
                //Nothing to add
            }
            override fun onPartialResults(partialResults: Bundle?) {
                //Nothing to add
            }
            override fun onEvent(eventType: Int, params: Bundle?) {
                //Nothing to add
            }
        })
    }

    private fun startListeningForWord() {
        Log.d("VoiceService", "startListeningForWord")
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            if (!isOnline()) {
                Log.d("VoiceService", "There is not internet connection, using the recognition without internet")
            }
            speechRecognizer?.startListening(recognizerIntent)
        } else {
            Log.e("VoiceService", "RECORD_AUDIO: Permission denied")
        }
    }

    private fun actionOnWakeWord() {
        stopSelf()
        val broadcastIntent: Intent
        Log.d("VoiceService", "Start action")
        if (isOnline()) {
            broadcastIntent = Intent(ACTIVE_LISTENING_ON)
        } else {
            val response = "There is not internet service, please try again later"
            broadcastIntent = Intent(TTS_RESPONSE).apply {
                putExtra("response", response)
            }
        }
        sendBroadcast(broadcastIntent)
    }

    override fun onDestroy() {
        speechRecognizer?.destroy()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "voice_service_channel",
                "Voice service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "voice_service_channel")
            .setContentTitle("Harley Voice Service")
            .setContentText("Listening commands ...")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setOngoing(true)
            .build()
    }
}