package com.wizeline.panamexicans.presentation.voicerecognition

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.wizeline.panamexicans.voice.Constants.ACTIVE_LISTENING_ON
import com.wizeline.panamexicans.voice.Constants.STOP_VOICE_SERVICE
import com.wizeline.panamexicans.voice.Constants.TTS_RESPONSE
import com.wizeline.panamexicans.voice.SpeechToTextManager
import com.wizeline.panamexicans.voice.TextToSpeechService
import com.wizeline.panamexicans.voice.isOnline

@Composable
fun VoiceRecognitionRoot(navController: NavController, viewModel: VoiceRecognitionViewModel) {
    //val uiState by viewModel.uiState.collectAsState()

    VoiceRecognitionScreen(
        viewModel = viewModel
    )
}

@Composable
fun VoiceRecognitionScreen(viewModel: VoiceRecognitionViewModel) {
    var ttsService: TextToSpeechService? = null
    val context = LocalContext.current
    val activity = LocalActivity.current
    ttsService = TextToSpeechService(activity!!)
    viewModel.checkSoftListeningStatus(context)

    viewModel.updateOnlineStatus(context.isOnline())

    DisposableEffect(context) {
        // Definimos el BroadcastReceiver
        val serviceStateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    STOP_VOICE_SERVICE -> {
                        viewModel.updateSoftListening(false)
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
                        viewModel.updateActiveListening(true)
                        val speechManager = SpeechToTextManager(context!!)
                        speechManager.startListening(
                            onResult = { recognizedText ->
                                Log.d("VoiceService", "Texto reconocido: $recognizedText")
                                // Por ejemplo, enviar el prompt a Gemini:
                                //viewModel.generateResponseFromPrompt(recognizedText)
                                viewModel.startVoiceService(
                                    context
                                )
                                ttsService.speak(recognizedText)
                                //viewModel.synthesizeSpeech(recognizedText)
                            },
                            onError = { errorMsg ->
                                Log.e("VoiceService", errorMsg)
                            },
                            onSilenceDetected = {
                                Log.d("VoiceService", "Silence detected")
                                viewModel.updateActiveListening(
                                    false
                                )
                                speechManager.stopListening()
                                viewModel.startVoiceService(
                                    context
                                )
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
                Context.RECEIVER_EXPORTED
            )
        } else {
            context.registerReceiver(
                serviceStateReceiver,
                intentFilter
            )
        }

        onDispose {
            context.unregisterReceiver(serviceStateReceiver)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            ttsService.shutdown()
        }
    }
    val isOnline = viewModel.isOnline.value ?: false
    val hasPermission = true
    val activeListening by viewModel.isActiveListening.observeAsState(initial = false)
    val softListening by viewModel.isSoftListening.observeAsState(initial = false)
    Box {
        VoiceAssistantScreen(
            context = context,
            hasPermission = hasPermission,
            isOnline = isOnline,
            isSoftListening = softListening,
            isActiveListening = activeListening,
            onRequestPermission = { //requestPermissions()
            },
            onStartVoiceService = {
                viewModel.startVoiceService(context)
            },
            onStopVoiceService = {
                viewModel.updateSoftListening(false)
                viewModel.stopVoiceService(context)
            }
        )
    }
}

@Composable
fun VoiceAssistantScreen(
    context: Context,
    isOnline: Boolean,
    hasPermission: Boolean,
    isSoftListening: Boolean,
    isActiveListening: Boolean,
    onRequestPermission: () -> Unit,
    onStartVoiceService: () -> Unit,
    onStopVoiceService: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (!isOnline) {
                Toast.makeText(context, "The device is offline", Toast.LENGTH_SHORT).show()
            }
            if (isSoftListening) {
                Toast.makeText(context, "Service activated", Toast.LENGTH_SHORT).show()
            }
            Text(
                text = "Voice Service",
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (hasPermission) "Activate the voice service"
                else "Microphone permission is required"
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (hasPermission) {
                        onStartVoiceService()
                    } else {
                        onRequestPermission()
                    }
                },
                enabled = !isSoftListening
            ) {
                Text(
                    text = when {
                        isSoftListening -> "Running"
                        hasPermission -> "Start voice service"
                        else -> "Request permission"
                    }
                )
            }

            if (isSoftListening) {
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onStopVoiceService()
                    }
                ) {
                    Text(text = "Stop voice service")
                }
            }

            if (isActiveListening) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { },
                    enabled = false
                ) {
                    Text(
                        text = "Active Listening: Active"
                    )
                }
            }
        }

        if (isSoftListening) {
            EdgeLightingView(isActiveListening = false)
        } else if (isActiveListening) {
            EdgeLightingView(isActiveListening = true)
        }
    }
}

@Composable
fun EdgeLightingView(isActiveListening: Boolean) {
    var animationProgress by remember { mutableFloatStateOf(0f) }
    val animation = remember { Animatable(0f) }

    var isBlinking by remember { mutableStateOf(false) }
    var blinkCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        animate(animation, 1f, 500) { progress ->
            animationProgress = progress
        }

        animate(animation, 0f, 500) { _ ->
            animationProgress = 0f
        }

        isBlinking = true
        repeat(2) {
            animate(animation, 1f, 500) { _ ->
                animationProgress = 1f
            }
            animate(animation, 0f, 500) { _ ->
                animationProgress = 0f
            }
            blinkCount++
        }

        isBlinking = false

        if (isActiveListening) {
            animate(animation, 1f, 500) { _ ->
                animationProgress = 1f
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val strokeWidth = 5.dp.toPx()

            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(canvasWidth, 0f)
                lineTo(canvasWidth, canvasHeight)
                lineTo(0f, canvasHeight)
                close()
            }

            val pathMeasure = PathMeasure()
            pathMeasure.setPath(path, false)
            val totalLength = pathMeasure.length

            val visibleLength = if (isBlinking) {
                if (animationProgress > 0.5f) totalLength else 0f
            } else {
                totalLength * animationProgress
            }

            val animatedPath = Path()
            pathMeasure.getSegment(0f, visibleLength, animatedPath, true)

            val orangeGradient = listOf(
                Color(0xFFFFA500),
                Color(0xFFFF4500),
                Color(0xFFFFD700)
            )
            val greenGradient = listOf(
                Color(0xFF32CD32),
                Color(0xFF228B22),
                Color(0xFFADFF2F)
            )

            val gradientColors = if (isActiveListening) orangeGradient else greenGradient

            val gradientBrush = Brush.linearGradient(
                colors = gradientColors,
                start = Offset(0f, 0f),
                end = Offset(canvasWidth, canvasHeight)
            )

            drawPath(
                path = animatedPath,
                brush = gradientBrush,
                style = Stroke(width = strokeWidth)
            )
        }
    }
}

private suspend fun animate(
    animation: Animatable<Float, *>,
    targetValue: Float,
    duration: Int,
    onUpdate: (Float) -> Unit
) {

    animation.animateTo(
        targetValue = targetValue,
        animationSpec = tween(
            durationMillis = duration,
            easing = FastOutSlowInEasing
        ),
    ) {
        onUpdate(value)
    }
}
