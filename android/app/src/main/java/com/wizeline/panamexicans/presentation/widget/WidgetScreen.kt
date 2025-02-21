package com.wizeline.panamexicans.presentation.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.wizeline.panamexicans.R
import com.wizeline.panamexicans.presentation.composables.PrimaryColorButton

@Composable
fun WidgetRoot(navController: NavController, viewModel: WidgetViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    WidgetScreen(
        uiState = uiState,
        onEvent = { event ->
            when (event) {

                else -> Unit
            }
            viewModel.onEvent(event)
        }
    )
}

@Composable
fun WidgetScreen(
    onEvent: (WidgetUiEvents) -> Unit,
    uiState: WidgetUiState
) {

    val appName = stringResource(id = R.string.app_name)
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add the '$appName' widget to your home screen and start a riding session to test the updates.",
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(30.dp))
        PrimaryColorButton(
            modifier = Modifier.fillMaxWidth(.5f),
            text = "Reset miles",
            onClick = { onEvent(WidgetUiEvents.OnClickResetMiles) })
    }
}

@Preview(showBackground = true)
@Composable
private fun WidgetScreenPreview() {
    WidgetScreen(onEvent = {}, uiState = WidgetUiState())
}
