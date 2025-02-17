package com.wizeline.panamexicans.presentation.iconupdater

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController

@Composable
fun IconUpdaterRoot(navController: NavController, viewModel: IconUpdaterViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    IconUpdaterScreen(
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
fun IconUpdaterScreen(
    onEvent: (IconUpdaterUiEvents) -> Unit,
    uiState: IconUpdaterUiState
) {
    Box {
        SubscriptionIconButton(modifier = Modifier.align(Alignment.Center), uiState, onEvent)
    }
}

@Composable
fun SubscriptionIconButton(
    modifier: Modifier,
    uiState: IconUpdaterUiState,
    onEvent: (IconUpdaterUiEvents) -> Unit
) {
    Button(
        modifier = modifier,
        onClick = {
            onEvent(IconUpdaterUiEvents.OnToggleIconClicked(uiState.isSubscribed))
        }
    ) {
        if (uiState.isSubscribed) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Premium Icon"
            )
            Text(text = "Premium Activated")
        } else {
            Icon(
                imageVector = Icons.Outlined.Star,
                contentDescription = "Default Icon"
            )
            Text(text = "Activate Premium")
        }
    }
}

@Preview
@Composable
private fun IconUpdaterScreenPreview() {
    IconUpdaterScreen(onEvent = {}, uiState = IconUpdaterUiState())
}
