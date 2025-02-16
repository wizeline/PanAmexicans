package com.wizeline.panamexicans.presentation.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wizeline.panamexicans.data.authentication.Authentication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authentication: Authentication) :
    ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    private val _uiAction = MutableSharedFlow<LoginUiAction>()
    val uiAction: SharedFlow<LoginUiAction> get() = _uiAction

    fun onEvent(event: LoginUiEvents) {
        when (event) {
            is LoginUiEvents.OnEmailChanged -> {
                onEmailChanged(event.email)
            }

            is LoginUiEvents.OnPasswordChanged -> {
                onPasswordChanged(event.password)
            }

            is LoginUiEvents.OnTogglePasswordVisibility -> {
                togglePasswordVisibility()
            }

            is LoginUiEvents.OnSignInClicked -> {
                onLoginClicked(_uiState.value.email, _uiState.value.password)
            }

            else -> Unit
        }
    }

    private fun onLoginClicked(email: String, password: String) {
        val isValidEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        _uiState.update { it.copy(invalidEmail = !isValidEmail) }
        if (!isValidEmail) {
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                authentication.login(email, password)
                _uiAction.emit(LoginUiAction.NavigateToMain)
            } catch (ex: Exception) {
                //_uiState.update { it.copy() }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !_uiState.value.passwordVisible) }
    }

    private fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    private fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password) }
    }
}

data class LoginUiState(
    val isLoading: Boolean = false,
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val invalidEmail: Boolean = false,
) {
    val loginButtonEnabled: Boolean = email.isNotEmpty() && password.isNotEmpty()
    val isEmailError: Boolean get() = email.isNotEmpty()
    val isPasswordError: Boolean get() = password.isNotEmpty()
}

sealed class LoginUiAction {
    data object NavigateToMain : LoginUiAction()
}

sealed interface LoginUiEvents {
    data class OnEmailChanged(val email: String) : LoginUiEvents
    data class OnPasswordChanged(val password: String) : LoginUiEvents

    data object OnBackPressed : LoginUiEvents
    data object OnSignInClicked : LoginUiEvents
    data object OnCreateAccountClicked : LoginUiEvents
    data object OnForgotPasswordClicked : LoginUiEvents
    data object OnTogglePasswordVisibility : LoginUiEvents
}