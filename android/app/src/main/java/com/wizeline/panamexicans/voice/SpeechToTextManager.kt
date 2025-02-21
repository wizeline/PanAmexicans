package com.wizeline.panamexicans.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

class SpeechToTextManager(private val context: Context) {
    private var speechRecognizer: SpeechRecognizer? = null
    private var recognitionListener: RecognitionListener? = null

    private var silenceTimer: CountDownTimer? = null
    private val silenceThreshold = 3.0f
    private val silenceDuration = 1500L

    fun startListening(
        onResult: (String) -> Unit,
        onError: (String) -> Unit,
        onSilenceDetected: () -> Unit
    ) {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
        recognitionListener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("SpeechToTextManager", "Ready for speech")
            }
            override fun onBeginningOfSpeech() {
                Log.d("SpeechToTextManager", "Speech beginning")
                silenceTimer?.cancel()
            }
            override fun onRmsChanged(rmsdB: Float) {
                if (rmsdB < silenceThreshold) {
                    if (silenceTimer == null) {
                        silenceTimer = object : CountDownTimer(silenceDuration, silenceDuration) {
                            override fun onTick(millisUntilFinished: Long) {}
                            override fun onFinish() {
                                onSilenceDetected()
                                silenceTimer = null
                            }
                        }.start()
                    }
                } else {
                    silenceTimer?.cancel()
                    silenceTimer = null
                }
            }
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                Log.d("SpeechToTextManager", "Speech ended")
                silenceTimer?.cancel()
                silenceTimer = null
            }
            override fun onError(error: Int) {
                onError("Speech recognition error: $error")
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val recognizedText = matches?.firstOrNull() ?: ""
                onResult(recognizedText)
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
        speechRecognizer?.setRecognitionListener(recognitionListener)
        speechRecognizer?.startListening(intent)
    }

    fun stopListening() {
        silenceTimer?.cancel()
        silenceTimer = null
        speechRecognizer?.stopListening()
        recognitionListener = null
    }

    fun destroy() {
        silenceTimer?.cancel()
        silenceTimer = null
        speechRecognizer?.destroy()
    }
}