package com.wizeline.panamexicans.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.maps.model.LatLng
import com.wizeline.panamexicans.R
import com.wizeline.panamexicans.data.models.Author
import com.wizeline.panamexicans.data.models.ChatBotResponseWithRouteImage
import com.wizeline.panamexicans.data.models.ChatMessage
import com.wizeline.panamexicans.data.models.GeneratedRoutImage
import com.wizeline.panamexicans.presentation.main.routegenerator.RouteGeneratorUiEvents
import com.wizeline.panamexicans.presentation.theme.Orange
import kotlinx.coroutines.launch

@Composable
fun ChatItem(
    chat: ChatMessage,
    displayTakeMeThereButton: Boolean = true,
    onMenuClicked: (String) -> Unit,
    onTakeMeThereClicked: (List<LatLng>) -> Unit
) {
    if (chat.isMenu()) {
        MenuItem(onMenuClicked, chat)
        return
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        contentAlignment = if (chat.author == Author.Me) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Row {
            if (chat.isMe().not()) {
                Image(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(id = R.drawable.ic_logo_premium),
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            if (chat.response.message.isBlank()) return
            Box(
                modifier = Modifier
                    .fillMaxWidth(.8f)
                    .background(
                        if (chat.isMe()) Orange else MaterialTheme.colorScheme.secondaryContainer,
                        if (chat.isMe()) RoundedCornerShape(
                            topStart = 8.dp,
                            topEnd = 8.dp,
                            bottomStart = 8.dp
                        ) else RoundedCornerShape(
                            topEnd = 8.dp,
                            bottomEnd = 8.dp,
                            bottomStart = 8.dp
                        )
                    )
                    .padding(4.dp)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = chat.response.message.trim(),
                        color = if (chat.isMe()) Color.White else MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier

                    )
                    chat.response.route?.let {
                        RouteItem(
                            chat.response,
                            displayTakeMeThereButton,
                            onTakeMeThereClicked = { onTakeMeThereClicked(it) })
                    }
                }
            }
        }
    }
}

@Composable
fun RouteItem(
    route: ChatBotResponseWithRouteImage,
    displayTakeMeThereButton: Boolean,
    onTakeMeThereClicked: (List<LatLng>) -> Unit
) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        ZoomableImage(
            route.route?.routeImage.orEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .zIndex(1f),
            onDismiss = {}
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (displayTakeMeThereButton) {
            PrimaryColorButton(
                Modifier
                    .fillMaxWidth(.8f)
                    .padding(horizontal = 16.dp),
                text = "Take me there",
                onClick = { onTakeMeThereClicked(route.waypoints) })
        }
    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PreferencesItem(
    onEvent: (RouteGeneratorUiEvents) -> Unit,
    selectedStates: List<Pair<String, Boolean>>
) {
    Column {
        Text(text = "Select your trip preferences")

        FlowRow(
            Modifier
                .fillMaxWidth(1f)
                .wrapContentHeight(align = Alignment.Top),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            selectedStates.fastForEachIndexed { index, element ->
                val isSelected = element.second
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        onEvent(RouteGeneratorUiEvents.OnPreferenceClicked(index))
                    },
                    label = { Text(text = element.first) },
                    leadingIcon =
                    if (isSelected) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Localized Description",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else {
                        null
                    }
                )
            }
        }
        Row(Modifier.fillMaxWidth()) {
            Text(text = "Available hours: ")

        }
    }
}

@Composable
private fun MenuItem(
    onMenuClicked: (String) -> Unit,
    chat: ChatMessage
) {
    Box(Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth(.8f)
                .border(1.dp, Orange, RoundedCornerShape(16.dp))
                .clickable { onMenuClicked(chat.prompt) }
                .padding(4.dp)
                .align(Alignment.Center)
        ) {
            Text(
                text = chat.response.message.trim(),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
    Spacer(modifier = Modifier.height(4.dp))
}


@Composable
fun ZoomableImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    // Estado para el zoom y la posición
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    // Aplicar el zoom y el desplazamiento
                    scale = (scale * zoom).coerceIn(1f, 5f) // Limitar el zoom entre 1x y 5x
                    offset = Offset(offset.x + pan.x, offset.y + pan.y)
                }
            }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.changes.all { !it.pressed }) {
                                // Si el usuario suelta el gesto, reinicia el zoom y la posición
                                coroutineScope.launch {
                                    scale = 1f
                                    offset = Offset.Zero
                                }
                            }
                        }
                    }
                },
            contentScale = ContentScale.Crop
        )
    }
}

@Preview
@Composable
private fun RouteItemPrev() {
    RouteItem(ChatBotResponseWithRouteImage("someUrl", route = null, emptyList()), false) {}
}

@Preview
@Composable
private fun PreferencesItemPrev() {
    ChatItem(
        ChatMessage(
            response = ChatBotResponseWithRouteImage(
                message = "the best route",
                null, emptyList()
            ),
            author = Author.Bot
        ), true, {}, {})
}