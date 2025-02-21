package com.wizeline.panamexicans.voice

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object Constants {
    const val STOP_VOICE_SERVICE = "VOICE_SERVICE_STOPPED"
    const val TTS_RESPONSE = "VOICE_TTS_RESPONSE"
    const val ACTIVE_LISTENING_ON = "ACTIVE_LISTENING_ON"

    const val ACTIVE_WORD = "hey harley"
    const val DEACTIVATED_WORD = "stop"
}

fun Context.isOnline(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork
    val capabilities = cm.getNetworkCapabilities(network)
    return capabilities != null &&
            (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
}