package com.wizeline.panamexicans.data.subscription

interface SubscriptionManager {
    fun setSubscriptionStatus(isSubscribed: Boolean)
    fun getSubscriptionStatus(): Boolean
}