package com.wizeline.panamexicans.data

import android.content.Context
import android.content.SharedPreferences

private const val PREFS_NAME = "location_preferences"
private const val KEY_SPEED = "key_speed"
private const val KEY_LATITUDE = "key_latitude"
private const val KEY_LONGITUDE = "key_longitude"

class LocationPreferenceManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveLocation(latitude: Double, longitude: Double) {
        sharedPreferences.edit().apply {
            putString(KEY_LATITUDE, latitude.toString())
            putString(KEY_LONGITUDE, longitude.toString())
            apply()
        }
    }

    fun getLocation(): Pair<Double, Double>? {
        val lat = sharedPreferences.getString(KEY_LATITUDE, null)?.toDoubleOrNull()
        val lng = sharedPreferences.getString(KEY_LONGITUDE, null)?.toDoubleOrNull()
        return if (lat != null && lng != null) Pair(lat, lng) else null
    }

    fun saveSpeed(speed: Float) {
        sharedPreferences.edit().apply {
            putFloat(KEY_SPEED, speed)
            apply()
        }
    }

    fun getSpeedMetersPerSecond(): Float {
        return sharedPreferences.getFloat(KEY_SPEED, 0f)
    }

}
