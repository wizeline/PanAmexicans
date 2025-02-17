package com.wizeline.panamexicans

import android.app.Application
import com.wizeline.panamexicans.data.subscription.SubscriptionManager
import com.wizeline.panamexicans.heresdk.HereSDKInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class PanAmexicanApplication : Application() {

    @Inject
    lateinit var subscriptionManager: SubscriptionManager

    private val hereSDKInitializer = HereSDKInitializer()

    override fun onCreate() {
        super.onCreate()
        //val isSubscribed = subscriptionManager.getSubscriptionStatus()
        //subscriptionManager.setSubscriptionStatus(isSubscribed)
        hereSDKInitializer.initializeSDK(this)
    }
}