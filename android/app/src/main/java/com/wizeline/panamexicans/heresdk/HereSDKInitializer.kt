package com.wizeline.panamexicans.heresdk

import android.content.Context

class HereSDKInitializer {
    fun initializeSDK(context: Context) {
        val accessKeyID = "HERE_ACCESS_KEY_ID"
        val accessKeySecret = "HERE_ACCESS_KEY_SECRET"
        //val sdkOptions = SDKOptions(accessKeyID, accessKeySecret)
        //sdkOptions.actionOnCacheLock = SDKOptions.ActionOnCacheLock.KILL_LOCKING_APP
        //try {
        //    SDKNativeEngine.makeSharedInstance(context, sdkOptions)
        //} catch (e: InstantiationErrorException) {
        //    throw RuntimeException("Initialization of HERE SDK failed: " + e.error.name)
        //}
    }
}
