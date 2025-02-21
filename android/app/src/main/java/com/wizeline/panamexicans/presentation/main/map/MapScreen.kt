package com.wizeline.panamexicans.presentation.main.map

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
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
import com.wizeline.panamexicans.voice.Constants.ACTIVE_LISTENING_ON
import com.wizeline.panamexicans.voice.Constants.STOP_VOICE_SERVICE
import com.wizeline.panamexicans.voice.Constants.TTS_RESPONSE
import com.wizeline.panamexicans.voice.SpeechToTextManager
import com.wizeline.panamexicans.voice.TextToSpeechService
import com.wizeline.panamexicans.voice.VoiceInteractionService
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

                is MapUiEvents.OnToggleVoiceAssistant -> {
                    if (uiState.voiceAssistantEnabled) {
                        startVoiceService(context)
                    } else {
                        stopVoiceService(context)
                    }
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
    var ttsService: TextToSpeechService? = null
    val context = LocalContext.current
    val activity = LocalActivity.current
    ttsService = TextToSpeechService(activity!!)
    DisposableEffect(context) {
        // Definimos el BroadcastReceiver
        val serviceStateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    STOP_VOICE_SERVICE -> {
                        //viewModel.updateSoftListening(false)
                    }

                    TTS_RESPONSE -> {
                        val response = intent.getStringExtra("response")
                        if (response != null) {
                            //renderUI()
                            //ttsService?.speak(response)
                        }
                    }

                    ACTIVE_LISTENING_ON -> {
                        if (context == null) return
                        //viewModel.updateActiveListening(true)
                        val speechManager = SpeechToTextManager(context)
                        speechManager.startListening(
                            onResult = { recognizedText ->
                                Log.d("VoiceService", "Texto reconocido: $recognizedText")
                                onEvent(MapUiEvents.OnInstructionReceived(recognizedText))
                                //viewModel.generateResponseFromPrompt(recognizedText)
                                startVoiceService(
                                    context
                                )
                                //ttsService.speak(recognizedText)
                                //viewModel.synthesizeSpeech(recognizedText)
                            },
                            onError = { errorMsg ->
                                Log.e("VoiceService", errorMsg)
                            },
                            onSilenceDetected = {
                                Log.d("VoiceService", "Silence detected")
                                //viewModel.updateActiveListening(false)
                                speechManager.stopListening()
                                startVoiceService(context)
                            }
                        )
                    }
                }
            }
        }

        val intentFilter = IntentFilter().apply {
            addAction(STOP_VOICE_SERVICE)
            addAction(TTS_RESPONSE)
            addAction(ACTIVE_LISTENING_ON)
        }
        ContextCompat.registerReceiver(
            context,
            serviceStateReceiver,
            intentFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.registerReceiver(
                serviceStateReceiver,
                intentFilter,
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            context.registerReceiver(
                serviceStateReceiver,
                intentFilter
            )
        }

        // En onDispose desregistramos el receptor para evitar fugas de memoria
        onDispose {
            context.unregisterReceiver(serviceStateReceiver)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            ttsService.shutdown()
        }
    }
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
        IconButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp, end = 8.dp)
                .size(64.dp),
            onClick = { onEvent(MapUiEvents.OnToggleVoiceAssistant) },
            colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.background),
        ) {
            Icon(
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .size(64.dp)
                    .padding(6.dp),
                painter = painterResource(id = if (uiState.voiceAssistantEnabled) R.drawable.ic_mic_off else R.drawable.ic_mic_on),
                contentDescription = null
            )
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
        if (uiState.sessionJointId != null) {
            ExpandableStatusFab(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 186.dp, end = 8.dp),
                statusOptions = uiState.statusOptions,
                selectedOption = uiState.status,
                onStatusChange = { onEvent(MapUiEvents.OnStatusChanged(it)) })
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

@Composable
fun ExpandableStatusFab(
    modifier: Modifier,
    statusOptions: List<RideSessionStatus>,
    selectedOption: String,
    onStatusChange: (status: String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val options = statusOptions.map { status ->
        when (status) {
            RideSessionStatus.RIDING -> status.name to R.drawable.ic_biker_me
            RideSessionStatus.BATHROOM -> status.name to R.drawable.ic_bathroom_state
            RideSessionStatus.LUNCH -> status.name to R.drawable.ic_lunch
            RideSessionStatus.DANGER -> status.name to R.drawable.ic_warning
        }
    }

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        // Animated visibility for the expanded mini-FABs
        AnimatedVisibility(
            visible = expanded,
            enter = androidx.compose.animation.fadeIn(animationSpec = tween(300)),
            exit = androidx.compose.animation.fadeOut(animationSpec = tween(300))
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                options.filter { it.first != selectedOption }.forEach { (status, icon) ->
                    FloatingActionButton(
                        onClick = {
                            onStatusChange(status)
                            expanded = false // Collapse after selection
                        },
                        modifier = Modifier.size(48.dp),
                        containerColor = MaterialTheme.colorScheme.background,
                    ) {
                        Image(
                            modifier = Modifier.padding(6.dp),
                            painter = painterResource(id = icon),
                            contentDescription = status,
                        )
                    }
                }
            }
        }
        // Main FAB that toggles the expanded state
        FloatingActionButton(
            modifier = Modifier.size(64.dp),
            onClick = { expanded = !expanded },
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            Image(
                modifier = Modifier.padding(6.dp),
                painter = painterResource(id = options.first { it.first == selectedOption }.second),
                contentDescription = if (expanded) "Close Menu" else "Change Status",
            )
        }
    }
}

fun startVoiceService(context: Context) {
    val intent = Intent(context, VoiceInteractionService::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        ContextCompat.startForegroundService(context, intent)
    } else {
        context.startService(intent)
    }
}

fun stopVoiceService(context: Context) {
    val intent = Intent(context, VoiceInteractionService::class.java)
    context.stopService(intent)
}

@Preview(showBackground = true)
@Composable
private fun MapScreenPreview() {
    MapScreen(onEvent = {}, uiState = MapUiState())
}
