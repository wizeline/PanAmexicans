package com.wizeline.panamexicans.voice

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.core.content.ContextCompat
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ActiveListeningManager(private val context: Context) {

    private val sampleRate = 16000 // Frecuencia de muestreo (Hz)
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    private var audioRecord: AudioRecord? = null
    private var isRecording = false

    fun startRecording(
        onAudioData: (ByteArray) -> Unit,
        onSilenceDetected: () -> Unit
    ) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Log.e("ActiveListening", "RECORD_AUDIO permission not granted")
            return
        }

        if (bufferSize <= 0) {
            Log.e("ActiveListening", "Buffer size is not valid.")
            return
        }

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )
        } catch (e: SecurityException) {
            Log.e("ActiveListening", "SecurityException: RECORD_AUDIO permission might not be granted", e)
            return
        }

        if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
            Log.e("ActiveListening", "AudioRecord initialization failed.")
            return
        }

        audioRecord?.startRecording()
        isRecording = true

        Thread {
            val buffer = ByteArray(bufferSize)
            var silenceStartTime: Long? = null
            val silenceThreshold = 200.0
            val silenceDurationMillis = 1500L  // Time to determined silence and turn off the active listening

            while (isRecording) {
                val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (read > 0) {
                    onAudioData(buffer.copyOf(read))

                    val shortBuffer = ShortArray(read / 2)
                    ByteBuffer.wrap(buffer, 0, read)
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .asShortBuffer()
                        .get(shortBuffer)

                    val amplitude = shortBuffer.map { Math.abs(it.toInt()) }
                        .average()

                    if (amplitude < silenceThreshold) {
                        if (silenceStartTime == null) {
                            silenceStartTime = System.currentTimeMillis()
                        } else {
                            val elapsed = System.currentTimeMillis() - silenceStartTime
                            if (elapsed >= silenceDurationMillis) {
                                Log.d("ActiveListening", "Silence detected for $elapsed ms, stopping recording.")
                                onSilenceDetected()
                                break
                            }
                        }
                    } else {
                        silenceStartTime = null
                    }
                }
            }
        }.start()
    }

    fun stopRecording() {
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }
}