package com.wizeline.panamexicans.presentation.ridesessions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.wizeline.panamexicans.data.models.RideSession
import com.wizeline.panamexicans.data.models.UserStatus
import com.wizeline.panamexicans.presentation.composables.PrimaryColorButton
import com.wizeline.panamexicans.presentation.theme.LightBlue

@Composable
fun RideSessionsRoot(navController: NavController, viewModel: RideSessionsViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    RideSessionsScreen(
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
fun RideSessionsScreen(
    onEvent: (RideSessionsUiEvents) -> Unit,
    uiState: RideSessionsUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            PrimaryColorButton(
                enabled = uiState.generateSessionEnabled,
                modifier = Modifier.weight(1f),
                text = "Generate Session",
                onClick = { onEvent(RideSessionsUiEvents.OnGenerateSessionClicked) })

            Spacer(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .width(1.dp)
                    .height(40.dp)
            )
            PrimaryColorButton(
                modifier = Modifier.weight(1f),
                text = "Look for sessions",
                onClick = { onEvent(RideSessionsUiEvents.OnLookForSessionsClicked) })
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        AnimatedVisibility(
            visible = uiState.rideSessionContainerState != RideSessionContainerState.NONE,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxSize(),
                colors = CardDefaults.elevatedCardColors(containerColor = LightBlue),
            ) {
                when (uiState.rideSessionContainerState) {
                    RideSessionContainerState.SESSION_LIST -> {
                        RideSessionList(onEvent, uiState.sessionList)
                    }

                    RideSessionContainerState.ON_SESSION -> {
                        RideSessionActive(uiState.sessionJointName, uiState.sessionUserStatus)
                    }

                    RideSessionContainerState.NONE -> {}
                }
            }
        }
    }
}

@Composable
fun RideSessionActive(sessionJointName: String, sessionUserStatus: List<UserStatus>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = sessionJointName,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(sessionUserStatus) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${it.firstName} ${it.lastName}",
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Text(
                        text = "${it.lat} ${it.lon}",
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Text(
                        text = it.status,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    HorizontalDivider()
                }

            }
        }
    }
}

@Composable
fun RideSessionList(
    onEvent: (RideSessionsUiEvents) -> Unit,
    list: List<Pair<String, RideSession>> = emptyList()
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(list) {
            Text(
                text = it.second.rideSessionName,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onEvent(
                            RideSessionsUiEvents.OnSessionClicked(
                                sessionId = it.first,
                                sessionName = it.second.rideSessionName
                            )
                        )
                    }
                    .padding(16.dp)
            )
            HorizontalDivider()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RideSessionsScreenPreview() {
    RideSessionsScreen(
        onEvent = {},
        uiState = RideSessionsUiState(
            rideSessionContainerState = RideSessionContainerState.ON_SESSION,
            sessionJointName = "Someone's Ride Session"
        )
    )
}
