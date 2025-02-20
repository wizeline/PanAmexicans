package com.wizeline.panamexicans.presentation.crashdetector

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.wizeline.panamexicans.presentation.theme.Orange

@Composable
fun CrashDetectorRoot(navController: NavController, viewModel: CrashDetectorViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    CrashDetectorScreen(
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
fun CrashDetectorScreen(
    onEvent: (CrashDetectorUiEvents) -> Unit,
    uiState: CrashDetectorUiState
) {
    Column(modifier = Modifier.fillMaxSize()) {

        Text(text = "Crash timestamps: " + uiState.fallTimeStamps)
        if (uiState.isCrashDetected) {
            EmergencyCard(uiState = uiState)
        }
    }
}

@Composable
private fun EmergencyCard(uiState: CrashDetectorUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Orange),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Call 911!", fontSize = 25.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CrashDetectorScreenPreview() {
    CrashDetectorScreen(onEvent = {}, uiState = CrashDetectorUiState(isCrashDetected = true))
}
