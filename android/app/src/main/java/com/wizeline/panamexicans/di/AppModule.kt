package com.wizeline.panamexicans.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.wizeline.panamexicans.data.SharedDataPreferenceManager
import com.wizeline.panamexicans.data.authentication.Authentication
import com.wizeline.panamexicans.data.authentication.FirebaseAuthenticationImpl
import com.wizeline.panamexicans.data.directions.DirectionsRepository
import com.wizeline.panamexicans.data.directions.DirectionsRepositoryImpl
import com.wizeline.panamexicans.data.ridesessions.RideSessionRepository
import com.wizeline.panamexicans.data.ridesessions.RideSessionRepositoryImpl
import com.wizeline.panamexicans.data.shareddata.SharedDataRepository
import com.wizeline.panamexicans.data.shareddata.SharedDataRepositoryImpl
import com.wizeline.panamexicans.data.userdata.UserDataRepository
import com.wizeline.panamexicans.data.userdata.UserDataRepositoryImpl
import com.wizeline.panamexicans.di.NetworkModule.provideDirectionsService
import com.wizeline.panamexicans.di.NetworkModule.provideRetrofit
import com.wizeline.panamexicans.presentation.crashdetector.CrashDetector
import com.wizeline.panamexicans.presentation.crashdetector.CrashDetectorManager
import com.wizeline.panamexicans.presentation.widget.PanAmexWidgetUpdater
import com.wizeline.panamexicans.presentation.widget.PanAmexWidgetUpdaterImpl
import com.wizeline.panamexicans.presentation.widget.WidgetMockRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun providesAuthentication(): Authentication = FirebaseAuthenticationImpl()

    @Singleton
    @Provides
    fun providesRideSessionRepository(): RideSessionRepository = RideSessionRepositoryImpl()

    @Singleton
    @Provides
    fun providesSharedDataRepository(@ApplicationContext context: Context): SharedDataRepository =
        SharedDataRepositoryImpl(providesLocationPreferenceManager(context))

    @Provides
    fun providesFirebaseAuthentication(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun providesFirebaseDb(): FirebaseFirestore =
        FirebaseFirestore.getInstance()

    @Singleton
    @Provides
    fun providesUserDataRepository(): UserDataRepository =
        UserDataRepositoryImpl(providesFirebaseAuthentication(), providesFirebaseDb())

    @Singleton
    @Provides
    fun providesDirectionsRepository(): DirectionsRepository =
        DirectionsRepositoryImpl(provideDirectionsService(provideRetrofit()))


    @Provides
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    @Singleton
    @Provides
    fun providesLocationPreferenceManager(@ApplicationContext context: Context):
            SharedDataPreferenceManager = SharedDataPreferenceManager(context)

    @Singleton
    @Provides
    fun providesWidgetRepository(): WidgetMockRepository =
        WidgetMockRepository()

    @Singleton
    @Provides
    fun providesWidgetUpdater(
        @ApplicationContext context: Context
    ): PanAmexWidgetUpdater =
        PanAmexWidgetUpdaterImpl(context)


    @Singleton
    @Provides
    fun providesCrashDetectorManager(
        @ApplicationContext context: Context
    ): CrashDetector = CrashDetectorManager(context)

}
