package com.wizeline.panamexicans.utils

import kotlin.random.Random

fun getRandomLatLng(): Pair<Double, Double> {
    val latitude = Random.nextDouble(-90.0, 90.0)
    val longitude = Random.nextDouble(-180.0, 180.0)
    return Pair(latitude, longitude)
}