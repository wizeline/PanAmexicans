package com.wizeline.panamexicans.presentation.main.map

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
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
    //todo need to change view type, only following if I have not moved the map
    val cameraPositionState = rememberCameraPositionState {
        position =
            CameraPosition.fromLatLngZoom(
                LatLng(
                    uiState.currentLocation?.latitude ?: 0.0,
                    uiState.currentLocation?.longitude ?: 0.0
                ), 15f
            )
    }
    LaunchedEffect(uiState.currentLocation) {
        uiState.currentLocation?.let {
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
            cameraPositionState = cameraPositionState,
            onPOIClick = { poi ->
                onEvent(MapUiEvents.OnPoiClicked(poi))
            },
            properties = MapProperties().copy(isMyLocationEnabled = true),
        ) {
            uiState.currentLocation?.let {
                //AnimatedMarker(
                //    targetPosition = LatLng(it.latitude, it.longitude),
                //    title = "Mi ubicación"
                //)
            }
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

@Preview
@Composable
private fun MapScreenPreview() {
    MapScreen(onEvent = {}, uiState = MapUiState())
}
