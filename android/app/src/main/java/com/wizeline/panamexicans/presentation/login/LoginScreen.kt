package com.wizeline.panamexicans.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.wizeline.panamexicans.R
import com.wizeline.panamexicans.navigation.LoginNavHost
import com.wizeline.panamexicans.navigation.LoginRoute
import com.wizeline.panamexicans.navigation.MainNavRoute
import com.wizeline.panamexicans.presentation.composables.PrimaryColorButton
import com.wizeline.panamexicans.presentation.theme.DarkBlue

@Composable
fun LoginRoot(
    mainNavController: NavHostController,
    viewModel: LoginViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val loginNavController = rememberNavController()
    LaunchedEffect(viewModel.uiAction) {
        viewModel.uiAction.collect { event ->
            when (event) {
                is LoginUiAction.NavigateToMain -> {
                    mainNavController.navigate(MainNavRoute.HOME.toString()) {
                        popUpTo(LoginRoute.Login.toString()) { inclusive = true }
                    }
                }
            }
        }
    }
    LoginScreen(
        loginNavController = loginNavController,
        mainNavController = mainNavController,
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                is LoginUiEvents.OnBackPressed -> {
                    val backStack = loginNavController.currentBackStackEntry
                    if (backStack?.destination?.route == LoginRoute.Login.route) {
                        mainNavController.popBackStack()
                    } else {
                        loginNavController.popBackStack()
                    }
                }

                is LoginUiEvents.OnCreateAccountClicked -> loginNavController.navigate(LoginRoute.Register.route)
                else -> Unit
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onEvent: (LoginUiEvents) -> Unit,
    uiState: LoginUiState,
    mainNavController: NavHostController,
    loginNavController: NavHostController
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val currentBackStackEntry by loginNavController.currentBackStackEntryAsState()

    val titleRes = when (currentBackStackEntry?.destination?.route) {
        LoginRoute.Login.route -> LoginRoute.Login.title
        LoginRoute.Register.route -> LoginRoute.Register.title
        else -> R.string.login
    }

    Scaffold(
        Modifier
            .fillMaxSize()
            .background(Color.DarkGray)
            .clip(RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp))
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = stringResource(titleRes).uppercase(),
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onEvent(LoginUiEvents.OnBackPressed) },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.size(36.dp))
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
        ) {
            LoginNavHost(
                mainNavController = mainNavController,
                loginNavController = loginNavController,
                modifier = Modifier.padding(24.dp),
                uiState = uiState,
                onEvent = onEvent,
                snackbarHostState = snackbarHostState
            )
        }
    }
}

@Composable
fun LoginComposable(
    uiState: LoginUiState,
    onEvent: (LoginUiEvents) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier.fillMaxSize()
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
                onEvent(LoginUiEvents.OnEmailChanged(email))
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
            visualTransformation = if (!uiState.passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = uiState.isPasswordError,
            singleLine = true,
            trailingIcon = {
                val painter =
                    painterResource(id = if (uiState.passwordVisible) R.drawable.ic_visible_off else R.drawable.ic_visible)
                val description = if (uiState.passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { onEvent(LoginUiEvents.OnTogglePasswordVisibility) }) {
                    Icon(painter = painter, contentDescription = description)
                }
            },
            onValueChange = { password ->
                onEvent(LoginUiEvents.OnPasswordChanged(password))
            })

        Spacer(modifier = Modifier.height(16.dp))
        PrimaryColorButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.sign_in),
            enabled = uiState.loginButtonEnabled,
            onClick = { onEvent(LoginUiEvents.OnSignInClicked) })
        Spacer(modifier = Modifier.height(16.dp))
        SignInFooter(onCreateAccountClicked = { onEvent(LoginUiEvents.OnCreateAccountClicked) })
    }
}

@Composable
fun SignInFooter(onCreateAccountClicked: () -> Unit) {
    HorizontalDivider()
    Text(
        modifier = Modifier.padding(vertical = 16.dp),
        fontSize = 21.sp,
        text = stringResource(R.string.dont_have_account_yet).uppercase(),
    )
    PrimaryColorButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.create_an_account),
        onClick = { onCreateAccountClicked() })
}

@Composable
fun HighlightedText(text: String) {
    Text(
        buildAnnotatedString {
            append(text)
            withStyle(style = SpanStyle(color = DarkBlue)) {
                append(" *")
            }
        },
        color = MaterialTheme.colorScheme.onBackground,
    )
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    LoginScreen(
        onEvent = {},
        uiState = LoginUiState(),
        mainNavController = rememberNavController(),
        loginNavController = rememberNavController()
    )
}
