package com.wizeline.panamexicans.di

import com.wizeline.panamexicans.data.directions.DirectionsApi
import com.wizeline.panamexicans.data.gemini.GeminiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .build()
            chain.proceed(request)
        }.build()

    @Provides
    @Singleton
    @Named("gemini")
    fun provideGeminiRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGeminiService(@Named("gemini") retrofit: Retrofit): GeminiService {
        return retrofit.create(GeminiService::class.java)
    }

    @Provides
    @Singleton
    @Named("map")
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideDirectionsService(@Named("map") retrofit: Retrofit): DirectionsApi {
        return retrofit.create(DirectionsApi::class.java)
    }
}
