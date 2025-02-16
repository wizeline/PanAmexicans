package com.wizeline.panamexicans.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
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
import kotlin.math.log

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

            is RegisterUiEvents.OnNameChanged -> {
                _uiState.update { it.copy(name = event.name) }
            }

            is RegisterUiEvents.OnLastNameChanged -> {
                _uiState.update { it.copy(lastName = event.lastName) }
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
                authentication.createAccount(
                    email,
                    password,
                    _uiState.value.name,
                    _uiState.value.lastName
                )
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
    val invalidName: Boolean = false,
    val invalidEmail: Boolean = false,
    val invalidLastName: Boolean = false,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val lastName: String = "",
) {
    val createAccountEnabled: Boolean
        get() = name.isNotBlank()
                && lastName.isNotBlank()
                && email.isNotBlank()
                && password.isNotBlank()
}

sealed interface RegisterUiEvents {
    data class OnEmailChanged(val email: String) : RegisterUiEvents
    data class OnNameChanged(val name: String) : RegisterUiEvents
    data class OnLastNameChanged(val lastName: String) : RegisterUiEvents
    data class OnPasswordChanged(val password: String) : RegisterUiEvents
    data class OnCreateAccountClicked(val email: String, val password: String) : RegisterUiEvents
}

sealed class RegisterUiAction {
    data object OnAccountCreated : RegisterUiAction()
    data class OnAccountCreationFailed(val error: String) : RegisterUiAction()
}