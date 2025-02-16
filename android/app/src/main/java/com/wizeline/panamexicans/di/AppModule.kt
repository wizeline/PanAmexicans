package com.wizeline.panamexicans.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.wizeline.panamexicans.data.FirebaseCollections
import com.wizeline.panamexicans.data.authentication.Authentication
import com.wizeline.panamexicans.data.authentication.FirebaseAuthenticationImpl
import com.wizeline.panamexicans.data.ridesessions.RideSessionRepository
import com.wizeline.panamexicans.data.ridesessions.RideSessionRepositoryImpl
import com.wizeline.panamexicans.data.userdata.UserDataRepository
import com.wizeline.panamexicans.data.userdata.UserDataRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.intellij.lang.annotations.PrintFormat

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun providesAuthentication(): Authentication = FirebaseAuthenticationImpl()

    @Provides
    fun providesRideSessionRepository(): RideSessionRepository = RideSessionRepositoryImpl()

    @Provides
    fun providesFirebaseAuthentication(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun providesFirebaseDb(): FirebaseFirestore =
        FirebaseFirestore.getInstance()

    @Provides
    fun providesUserDataRepository(): UserDataRepository =
        UserDataRepositoryImpl(providesFirebaseAuthentication(), providesFirebaseDb())
}