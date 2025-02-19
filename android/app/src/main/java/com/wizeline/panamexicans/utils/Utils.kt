package com.wizeline.panamexicans.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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

fun getBitmapDescriptorFromVector(
    context: Context,
    vectorResId: Int,
    width: Int,
    height: Int
): BitmapDescriptor? {
    MapsInitializer.initialize(context)
    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    vectorDrawable.setBounds(0, 0, width, height)
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}