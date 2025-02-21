package com.wizeline.panamexicans.voice

import android.app.Activity
import android.speech.tts.TextToSpeech
import java.util.Locale

class TextToSpeechService(activity: Activity) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(activity, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.getDefault()
        }
    }

    fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun shutdown() {
        tts?.shutdown()
    }
}