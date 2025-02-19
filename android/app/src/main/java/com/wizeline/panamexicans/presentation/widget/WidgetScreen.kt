package com.wizeline.panamexicans.presentation.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.wizeline.panamexicans.presentation.composables.PrimaryColorButton

@Composable
fun WidgetRoot(navController: NavController, viewModel: WidgetViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    WidgetScreen(
        uiState = uiState,
        onEvent = { event ->
            when (event) {
                is WidgetUiEvents.OnClick -> {
                    viewModel.updateWidget()
                }
                else -> Unit
            }
        }
    )
}

@Composable
fun WidgetScreen(
    onEvent: (WidgetUiEvents) -> Unit,
    uiState: WidgetUiState
) {
    Column(modifier = Modifier.fillMaxSize()) {
        PrimaryColorButton(text = "Update widget miles", onClick = { onEvent(WidgetUiEvents.OnClick)})
    }
}

@Preview(showBackground = true)
@Composable
private fun WidgetScreenPreview() {
    WidgetScreen(onEvent = {}, uiState = WidgetUiState())
}
