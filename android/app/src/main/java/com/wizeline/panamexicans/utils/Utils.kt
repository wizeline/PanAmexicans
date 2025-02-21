package com.wizeline.panamexicans.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

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

inline fun <reified T : Enum<T>> randomEnumValue(): T {
    val values = enumValues<T>()
    return values.random()
}

fun callEmergency(context: Context) {
    val phoneNumber = "911"
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phoneNumber")
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "No dialer available", Toast.LENGTH_SHORT).show()
    }
}

fun calculateDistanceInMeters(lat1: Double?, lon1: Double?, lat2: Double?, lon2: Double?): Double? {
    if (lat2 == null || lon2 == null) {
        return null
    }

    val earthRadius = 6371e3
    val dLat = Math.toRadians(lat2.minus(lat1 ?: 0.0))
    val dLon = Math.toRadians(lon2.minus(lon1 ?: 0.0))
    val a = sin(dLat / 2).pow(2.0) +
            cos(Math.toRadians(lat1 ?: 0.0)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2).pow(2.0)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadius * c
}

fun metersToMiles(meters: Double): Double {
    return meters * 0.000621371
}