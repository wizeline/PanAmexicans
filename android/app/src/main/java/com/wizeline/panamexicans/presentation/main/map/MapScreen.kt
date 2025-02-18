package com.wizeline.panamexicans.presentation.main.map

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.wizeline.panamexicans.presentation.composables.PrimaryColorButton
import kotlinx.coroutines.launch

@Composable
fun MapRoot(modifier: Modifier = Modifier, viewModel: MapViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    MapScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                else -> Unit
            }
        }
    )
}

@Composable
fun MapScreen(
    onEvent: (MapUiEvents) -> Unit,
    uiState: MapUiState
) {
    val cameraPositionState = rememberCameraPositionState {
        position =
            CameraPosition.fromLatLngZoom(
                LatLng(
                    uiState.cachedLocation?.latitude ?: 0.0,
                    uiState.cachedLocation?.longitude ?: 0.0
                ), 15f
            )
    }
    LaunchedEffect(uiState.cachedLocation) {
        uiState.cachedLocation?.let {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(
                    LatLng(it.latitude, it.longitude),
                    15f
                ),
                durationMs = 1000
            )
        }
    }
    Box {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            uiSettings = MapUiSettings().copy(
                compassEnabled = true
            ),
            cameraPositionState = cameraPositionState,
            onPOIClick = { poi ->
                onEvent(MapUiEvents.OnPoiClicked(poi))
            },
            properties = MapProperties().copy(isMyLocationEnabled = true),
        ) {
            uiState.routePoints?.let {
                Polyline(
                    points = uiState.routePoints,
                    color = Color.Blue,
                    width = 8f
                )
            }
            uiState.otherRiders.forEach { user ->
                Marker(
                    state = MarkerState(position = LatLng(user.lat, user.lon),),
                    title = "User: ${user.id}"
                )
            }
        }
        if (uiState.rideSessionTitle.isNotBlank()) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xA7000000))
                    .padding(4.dp),
                textAlign = TextAlign.Center,
                text = uiState.rideSessionTitle,
                color = Color.White
            )
        }
        if (uiState.displayStartRideSession) {
            PrimaryColorButton(
                modifier = Modifier
                    .fillMaxWidth(.7f)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 36.dp),
                text = "Start a Shared session",
                onClick = { onEvent(MapUiEvents.OnStartSharedSessionClicked) })
        }
        if (uiState.displayPoiDialog) {
            uiState.lastPoiClicked?.let {
                TakeMeThereDialog(title = it.name,
                    onDismiss = { onEvent(MapUiEvents.OnDismissDialog) },
                    onTakeMeThereClicked = { onEvent(MapUiEvents.OnTakeMeThereClicked(it)) })
            }
        }
    }
}

@Composable
fun TakeMeThereDialog(
    title: String,
    onDismiss: () -> Unit,
    onTakeMeThereClicked: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title)
        },
        confirmButton = {
            Button(
                onClick = {
                    onTakeMeThereClicked()
                    onDismiss()
                }
            ) {
                Text("Take me there")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AnimatedMarker(
    targetPosition: LatLng,
    animationDuration: Int = 1000,
    title: String? = null,
    snippet: String? = null,
    icon: BitmapDescriptor? = null
) {
    val latAnim = remember { Animatable(targetPosition.latitude.toFloat()) }
    val lngAnim = remember { Animatable(targetPosition.longitude.toFloat()) }

    LaunchedEffect(targetPosition) {
        launch {
            latAnim.animateTo(
                targetValue = targetPosition.latitude.toFloat(),
                animationSpec = tween(durationMillis = animationDuration)
            )
        }
        launch {
            lngAnim.animateTo(
                targetValue = targetPosition.longitude.toFloat(),
                animationSpec = tween(durationMillis = animationDuration)
            )
        }
    }

    Marker(
        state = MarkerState(LatLng(latAnim.value.toDouble(), lngAnim.value.toDouble())),
        title = title,
        snippet = snippet,
        icon = icon
    )
}

@Preview(showBackground = true)
@Composable
private fun MapScreenPreview() {
    MapScreen(onEvent = {}, uiState = MapUiState())
}
