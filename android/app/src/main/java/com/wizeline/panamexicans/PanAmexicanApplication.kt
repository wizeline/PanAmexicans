package com.wizeline.panamexicans

import android.app.Application
import com.wizeline.panamexicans.heresdk.HereSDKInitializer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PanAmexicanApplication : Application() {
    private val hereSDKInitializer = HereSDKInitializer()

    override fun onCreate() {
        super.onCreate()
        hereSDKInitializer.initializeSDK(this)
    }
}