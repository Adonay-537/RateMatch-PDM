package com.example.com.pdm0126.ratematch.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.com.pdm0126.ratematch.data.repository.AuthRepository
import com.example.com.pdm0126.ratematch.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    // NUEVO: Estado para manejar mensajes de reseteo de contraseña
    private val _resetPasswordMessage = MutableStateFlow<String?>(null)
    val resetPasswordMessage: StateFlow<String?> = _resetPasswordMessage

    init {
        viewModelScope.launch {
            authRepository.refreshDynamicConfig()
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.login(email, pass)
            result.onSuccess {
                _authState.value = AuthState.Success(it)
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "Error desconocido")
            }
        }
    }

    fun register(email: String, pass: String, name: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.register(email, pass, name)
            result.onSuccess {
                _authState.value = AuthState.Success(it)
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "Error desconocido")
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _authState.value = AuthState.Idle
    }

    // =======================================================
    // NUEVO: Recuperación de Contraseña
    // =======================================================
    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _resetPasswordMessage.value = "Por favor, ingresa tu correo primero."
            return
        }
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnSuccessListener {
                _resetPasswordMessage.value = "¡Enlace enviado! Revisa tu bandeja de entrada o spam."
            }
            .addOnFailureListener { e ->
                _resetPasswordMessage.value = "Error al enviar: ${e.localizedMessage}"
            }
    }

    fun clearResetMessage() {
        _resetPasswordMessage.value = null
    }

    // =======================================================
    // NUEVO: Iniciar Sesión con Google
    // =======================================================
    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = FirebaseAuth.getInstance().signInWithCredential(credential).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    // Creamos el usuario basándonos en los datos de Google
                    val user = User(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        displayName = firebaseUser.displayName ?: "Usuario Google" // ¡Aquí estaba el detalle!
                    )
                    _authState.value = AuthState.Success(user)
                } else {
                    _authState.value = AuthState.Error("No se pudo autenticar con Google.")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Autenticación fallida.")
            }
        }
    }
}