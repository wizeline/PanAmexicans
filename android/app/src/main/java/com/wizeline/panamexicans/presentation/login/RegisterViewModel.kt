package com.wizeline.panamexicans.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wizeline.panamexicans.authentication.Authentication
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
class RegisterViewModel @Inject constructor(val authentication: Authentication) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()
    private val _uiAction = MutableSharedFlow<RegisterUiAction>()
    val uiAction: SharedFlow<RegisterUiAction> get() = _uiAction

    fun onEvent(event: RegisterUiEvents) {
        when (event) {
            is RegisterUiEvents.OnEmailChanged -> {
                _uiState.update { it.copy(email = event.email) }
            }

            is RegisterUiEvents.OnPasswordChanged -> {
                _uiState.update { it.copy(password = event.password) }
            }

            is RegisterUiEvents.OnCreateAccountClicked -> {
                registerAccount(event.email, event.password)
            }

            else -> Unit
        }
    }

    private fun registerAccount(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                authentication.createAccount(email, password)
                _uiAction.emit(RegisterUiAction.OnAccountCreated)
            } catch (ex: Exception) {
                _uiAction.emit(RegisterUiAction.OnAccountCreationFailed(ex.localizedMessage.orEmpty()))
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}

data class RegisterUiState(
    val isLoading: Boolean = false,
    val invalidEmail: Boolean = false,
    val email: String = "",
    val password: String = ""
) {
    val createAccountEnabled: Boolean
        get() = email.isNotBlank() && password.isNotBlank()
}

sealed interface RegisterUiEvents {
    data class OnEmailChanged(val email: String) : RegisterUiEvents
    data class OnPasswordChanged(val password: String) : RegisterUiEvents
    data class OnCreateAccountClicked(val email: String, val password: String) : RegisterUiEvents
}

sealed class RegisterUiAction {
    data object OnAccountCreated : RegisterUiAction()
    data class OnAccountCreationFailed(val error: String) : RegisterUiAction()
}