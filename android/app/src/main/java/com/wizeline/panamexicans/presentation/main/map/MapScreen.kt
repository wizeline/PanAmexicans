package com.wizeline.panamexicans.presentation.main.map

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.wizeline.panamexicans.R
import com.wizeline.panamexicans.data.ridesessions.RideSessionStatus
import com.wizeline.panamexicans.presentation.composables.PrimaryColorButton
import com.wizeline.panamexicans.utils.callEmergency
import com.wizeline.panamexicans.utils.getBitmapDescriptorFromVector
import kotlinx.coroutines.launch

@Composable
fun MapRoot(modifier: Modifier = Modifier, viewModel: MapViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.refreshSessionIfConnected()
    }

    MapScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                is MapUiEvents.OnCall911Clicked -> {
                    callEmergency(context)
                }

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
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState {
        position =
            CameraPosition.fromLatLngZoom(
                LatLng(
                    uiState.cachedLocation?.latitude ?: 0.0,
                    uiState.cachedLocation?.longitude ?: 0.0
                ), 15f
            )
    }
    val bikerIcon = remember {
        getBitmapDescriptorFromVector(context, R.drawable.ic_biker, width = 100, height = 100)
    }
    val bikerMeIcon = remember {
        getBitmapDescriptorFromVector(context, R.drawable.ic_biker_me, width = 100, height = 100)
    }
    val lunchIcon = remember {
        getBitmapDescriptorFromVector(context, R.drawable.ic_lunch, width = 100, height = 100)
    }
    val dangerIcon = remember {
        getBitmapDescriptorFromVector(context, R.drawable.ic_warning, width = 100, height = 100)
    }
    val bathroomIcon = remember {
        getBitmapDescriptorFromVector(
            context,
            R.drawable.ic_bathroom_state,
            width = 100,
            height = 100
        )
    }
    val isDarkTheme = isSystemInDarkTheme()
    val mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
        context,
        if (isDarkTheme) R.raw.map_style_dark else R.raw.map_style_light
    )
    LaunchedEffect(uiState.cachedLocation) {
        uiState.cachedLocation?.let {
            cameraPositionState.move(
                update = CameraUpdateFactory.newLatLngZoom(
                    LatLng(it.latitude, it.longitude),
                    15f
                ),
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
            properties = MapProperties().copy(
                isMyLocationEnabled = uiState.sessionJointId == null,
                mapStyleOptions = mapStyleOptions
            ),
        ) {
            uiState.routePoints?.let {
                Polyline(
                    points = uiState.routePoints,
                    color = Color.Blue,
                    width = 8f
                )
            }
            uiState.waypoints?.let { waypoints ->
                waypoints.forEach {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Waypoint",
                    )
                }
            }
            uiState.riders.forEach { user ->
                AnimatedMarker(
                    targetPosition = LatLng(user.lat, user.lon),
                    title = "${user.firstName} ${user.lastName}",
                    snippet = user.status,
                    onDangerClicked = { targetPosition, name ->
                        onEvent(
                            MapUiEvents.OnDangerClicked(
                                targetPosition = targetPosition,
                                name = name
                            )
                        )
                    },
                    icon = when (user.status) {
                        RideSessionStatus.RIDING.name -> if (user.id == uiState.myId) bikerMeIcon else bikerIcon
                        RideSessionStatus.DANGER.name -> dangerIcon
                        RideSessionStatus.LUNCH.name -> lunchIcon
                        RideSessionStatus.BATHROOM.name -> bathroomIcon
                        else -> bikerIcon
                    }
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
                    onTakeMeThereClicked = { onEvent(MapUiEvents.OnTakeMeThereClicked(it.latLng)) })
            }
        }
        if (uiState.displayDangerDialog) {
            ThreeButtonDialog(
                title = "${uiState.lastDangerName} is in danger",
                message = "Please assist him or call 911 if he is in an emergency",
                onCall911 = { onEvent(MapUiEvents.OnCall911Clicked) },
                onTakeMeThere = {
                    uiState.lastDangerLatLng?.let {
                        onEvent(MapUiEvents.OnTakeMeThereClicked(it))
                    }
                },
                onCancel = { onEvent(MapUiEvents.OnDismissDialog) }
            )
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
fun ThreeButtonDialog(
    title: String,
    message: String,
    onCall911: () -> Unit,
    onTakeMeThere: () -> Unit,
    onCancel: () -> Unit
) {
    Dialog(onDismissRequest = onCancel) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = message, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PrimaryColorButton(
                        modifier = Modifier.fillMaxWidth(.9f),
                        text = "Call 911",
                        onClick = onCall911
                    )
                    PrimaryColorButton(
                        modifier = Modifier.fillMaxWidth(.9f),
                        onClick = onTakeMeThere,
                        text = "Take me there"
                    )
                    PrimaryColorButton(
                        modifier = Modifier.fillMaxWidth(.9f),
                        onClick = onCancel,
                        text = "Cancel"
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedMarker(
    targetPosition: LatLng,
    animationDuration: Int = 1000,
    title: String? = null,
    snippet: String? = null,
    onDangerClicked: (LatLng, String) -> Unit,
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
        icon = icon,
        onClick = {
            if (snippet == RideSessionStatus.DANGER.name) {
                onDangerClicked(targetPosition, title.orEmpty())
            }
            false
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun MapScreenPreview() {
    MapScreen(onEvent = {}, uiState = MapUiState())
}
