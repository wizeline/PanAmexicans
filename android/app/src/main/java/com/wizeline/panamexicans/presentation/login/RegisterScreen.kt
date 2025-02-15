package com.wizeline.panamexicans.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wizeline.panamexicans.R
import com.wizeline.panamexicans.navigation.LoginRoute
import com.wizeline.panamexicans.navigation.MainNavRoute
import com.wizeline.panamexicans.presentation.composables.PrimaryColorButton
import com.wizeline.panamexicans.presentation.theme.DarkBlue
import com.wizeline.panamexicans.presentation.theme.PanAmexicansTheme

@Composable
fun RegisterRoot(mainNavController: NavController, loginNavController: NavController, viewModel: RegisterViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel.uiAction) {
        viewModel.uiAction.collect { event ->
            when (event) {
                is RegisterUiAction.OnAccountCreated -> {
                    mainNavController.navigate(MainNavRoute.HOME.toString()) {
                        popUpTo(LoginRoute.Login.toString()) { inclusive = true }
                    }
                }

                else -> Unit
            }
        }
    }

    RegisterScreen(
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
fun RegisterScreen(
    onEvent: (RegisterUiEvents) -> Unit,
    uiState: RegisterUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors().copy(
                focusedIndicatorColor = DarkBlue,
                focusedLabelColor = DarkBlue,
                unfocusedLabelColor = Color.Black,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedContainerColor = MaterialTheme.colorScheme.background
            ),
            value = uiState.email,
            label = {
                HighlightedText(
                    text = stringResource(R.string.email).uppercase(),
                )
            },
            supportingText = {
                if (uiState.invalidEmail) {
                    Text(
                        modifier = Modifier.testTag("SupportingText"),
                        color = DarkBlue,
                        text = stringResource(R.string.invalid_email)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            onValueChange = { email ->
                onEvent(RegisterUiEvents.OnEmailChanged(email))
            })
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors().copy(
                focusedIndicatorColor = DarkBlue,
                focusedLabelColor = DarkBlue,
                unfocusedLabelColor = Color.Black,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedContainerColor = MaterialTheme.colorScheme.background
            ),
            value = uiState.password,
            label = {
                HighlightedText(
                    text = stringResource(R.string.password).uppercase(),
                )
            },
            supportingText = {
                if (uiState.invalidEmail) {
                    Text(
                        modifier = Modifier.testTag("SupportingText"),
                        color = DarkBlue,
                        text = stringResource(R.string.invalid_password)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            onValueChange = { password ->
                onEvent(RegisterUiEvents.OnPasswordChanged(password))
            })
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryColorButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.create_an_account),
            enabled = uiState.createAccountEnabled,
            onClick = {
                onEvent(
                    RegisterUiEvents.OnCreateAccountClicked(
                        uiState.email,
                        uiState.password
                    )
                )
            })
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterScreenPreview() {
    PanAmexicansTheme {
        RegisterScreen(onEvent = {}, uiState = RegisterUiState())
    }
}
