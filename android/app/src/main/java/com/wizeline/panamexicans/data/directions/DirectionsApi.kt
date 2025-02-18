package com.wizeline.panamexicans.data.directions

import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsApi {
    @GET("directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") key: String
    ): DirectionsResponse
}