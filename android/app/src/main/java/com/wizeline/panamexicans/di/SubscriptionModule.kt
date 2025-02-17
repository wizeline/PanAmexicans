package com.wizeline.panamexicans.di

import com.wizeline.panamexicans.data.subscription.SubscriptionManager
import com.wizeline.panamexicans.data.subscription.SubscriptionManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SubscriptionModule {

    @Binds
    abstract fun bindSubscriptionManager(
        subscriptionManagerImpl: SubscriptionManagerImpl
    ): SubscriptionManager
}