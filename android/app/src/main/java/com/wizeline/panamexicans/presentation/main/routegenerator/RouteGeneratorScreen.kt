package com.wizeline.panamexicans.presentation.main.routegenerator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wizeline.panamexicans.presentation.composables.ChatItem
import com.wizeline.panamexicans.presentation.composables.PreferencesItem
import com.wizeline.panamexicans.presentation.theme.Orange

@Composable
fun RouteGeneratorRoot(viewModel: RouteGeneratorViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    RouteGeneratorScreen(
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
fun RouteGeneratorScreen(
    onEvent: (RouteGeneratorUiEvents) -> Unit,
    uiState: RouteGeneratorUiState
) {
    var userInput by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(uiState.messages) { message ->
                if (message.isPreferences()) {
                    PreferencesItem(onEvent, uiState.selectedStates)
                } else {
                    ChatItem(message, { })
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .weight(1f),
                value = userInput,
                singleLine = true,
                onValueChange = { userInput = it },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Orange,
                    unfocusedBorderColor = Orange
                ),
                placeholder = { Text("Include more preferences...") },
                trailingIcon = {
                    IconButton(modifier = Modifier
                        .size(48.dp),
                        onClick = {
                            onEvent(RouteGeneratorUiEvents.OnMessageSent(userInput))
                            userInput = ""
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                })
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RouteGeneratorScreenPreview() {
    RouteGeneratorScreen(onEvent = {}, uiState = RouteGeneratorUiState())
}
