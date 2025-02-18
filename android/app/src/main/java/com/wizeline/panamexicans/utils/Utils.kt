package com.wizeline.panamexicans.utils

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import kotlin.random.Random

fun getRandomLatLng(): Pair<Double, Double> {
    val latitude = Random.nextDouble(-90.0, 90.0)
    val longitude = Random.nextDouble(-180.0, 180.0)
    return Pair(latitude, longitude)
}

fun getRandomCoordinateInSanFrancisco(): LatLng {
    val minLat = 37.703399
    val maxLat = 37.812
    val minLon = -122.527
    val maxLon = -122.3482

    val randomLat = Random.nextDouble(minLat, maxLat)
    val randomLon = Random.nextDouble(minLon, maxLon)

    return LatLng(randomLat, randomLon)
}

fun Location.toLatLng(): LatLng {
    return LatLng(latitude, longitude)
}