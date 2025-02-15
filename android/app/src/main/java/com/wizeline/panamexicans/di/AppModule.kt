package com.wizeline.panamexicans.di

import com.wizeline.panamexicans.authentication.Authentication
import com.wizeline.panamexicans.authentication.FirebaseAuthenticationImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun providesAuthentication(): Authentication = FirebaseAuthenticationImpl()

}