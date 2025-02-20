package com.wizeline.panamexicans.presentation.crashdetector

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CrashDetectorManager(
    private val context: Context,
) : CrashDetector, SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val _crashState: MutableStateFlow<CrashState> = MutableStateFlow(CrashState())
    override val crashState: StateFlow<CrashState>
        get() = _crashState.asStateFlow()

    private var isFalling: Boolean = false
    private var lastFallTime: Long = 0

    override fun startListening() {
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            fallDetector(event = it)
        }
    }

    private fun fallDetector(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        // Calculate acceleration magnitude
        val acceleration = Math.sqrt((x * x + y * y + z * z).toDouble())

        // Check if the phone is in free fall (acceleration less than threshold, e.g., 2 m/s²)
        if (acceleration < 2.0) {
            if (!isFalling) {
                isFalling = true
                _crashState.update {
                    it.copy(
                        isFalling = isFalling,
                        isCrashRisk = false,
                    )
                }
            }
        } else {
            if (isFalling) {
                // The fall has finished with time threshold (acceleration above threshold)
                if (System.currentTimeMillis() - lastFallTime > 500) {
                    isFalling = false
                    val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                    _crashState.update {
                        it.copy(
                            isFalling = isFalling,
                            isCrashRisk = true,
                            fallTimeStamps = _crashState.value.fallTimeStamps + timestamp
                        )
                    }
                }
            }
        }

        if (!isFalling) {
            lastFallTime = System.currentTimeMillis()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

}
