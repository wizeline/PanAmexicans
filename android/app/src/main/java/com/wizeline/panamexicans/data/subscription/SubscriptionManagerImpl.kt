package com.wizeline.panamexicans.data.subscription

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val DEFAULT_ICON_ALIAS = "com.wizeline.panamexicans.DefaultIconAlias"
private const val PREMIUM_ICON_ALIAS = "com.wizeline.panamexicans.PremiumIconAlias"

@Singleton
class SubscriptionManagerImpl @Inject constructor(@ApplicationContext private val context: Context) :
    SubscriptionManager {

    override fun setSubscriptionStatus(isSubscribed: Boolean) {
        val sharedPref = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean("is_subscribed", isSubscribed).apply()
        updateAppIcon(isSubscribed)
    }

    override fun getSubscriptionStatus(): Boolean {
        val sharedPref = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("is_subscribed", false)
    }

    private fun updateAppIcon(subscribed: Boolean) {
        val packageManager = context.packageManager
        val defaultIconAlias = ComponentName(context, DEFAULT_ICON_ALIAS)
        val premiumIconAlias = ComponentName(context, PREMIUM_ICON_ALIAS)

        if (subscribed) {
            enableIcon(packageManager, premiumIconAlias)
            disableIcon(packageManager, defaultIconAlias)
        } else {
            disableIcon(packageManager, premiumIconAlias)
            enableIcon(packageManager, defaultIconAlias)
        }
    }

    private fun disableIcon(
        packageManager: PackageManager,
        defaultIconAlias: ComponentName
    ) {
        packageManager.setComponentEnabledSetting(
            defaultIconAlias,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    private fun enableIcon(
        packageManager: PackageManager,
        premiumIconAlias: ComponentName
    ) {
        packageManager.setComponentEnabledSetting(
            premiumIconAlias,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }
}