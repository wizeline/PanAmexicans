package com.wizeline.panamexicans.presentation.main.sessions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wizeline.panamexicans.data.models.UserStatus

@Composable
fun MapSessionsRoot(viewModel: MapSessionsViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    MapSessionsScreen(
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
fun MapSessionsScreen(
    onEvent: (MapSessionsUiEvents) -> Unit,
    uiState: MapSessionsUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 32.dp)
    ) {
        ActiveSessionTitle(onEvent)
        ActiveSessions(uiState, onEvent)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = uiState.sessionJointName,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(uiState.sessionUserStatus) {
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
}

@Composable
private fun ActiveSessionTitle(onEvent: (MapSessionsUiEvents) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 32.dp, end = 16.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(1f),
            text = "Active ride sessions",
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = { onEvent(MapSessionsUiEvents.OnRefreshClicked) }) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
        }
    }
}

@Composable
private fun ActiveSessions(
    uiState: MapSessionsUiState,
    onEvent: (MapSessionsUiEvents) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(enabled = false) { }
            .padding(horizontal = 16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        if (uiState.sessionList.isNotEmpty()) {
            LazyColumn(Modifier.fillMaxSize()) {
                itemsIndexed(uiState.sessionList) { index, item ->
                    ListItem(
                        modifier = Modifier.clickable {
                            onEvent(
                                MapSessionsUiEvents.OnSessionClicked(
                                    item.first,
                                    item.second.rideSessionName
                                )
                            )
                        },
                        headlineContent = {
                            Text(text = item.second.rideSessionName)
                        })
                    if (index != uiState.sessionList.size - 1) HorizontalDivider()
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    text = "There are no active sessions available, try to refresh"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MapSessionsScreenPreview() {
    MapSessionsScreen(onEvent = {}, uiState = MapSessionsUiState())
}
