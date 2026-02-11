package com.magnuschess.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magnuschess.data.model.User
import com.magnuschess.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    init {
        checkCurrentUser()
    }
    
    private fun checkCurrentUser() {
        viewModelScope.launch {
            authRepository.getCurrentUser().fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    if (user != null) {
                        _authState.value = AuthState.Success(user)
                    }
                },
                onFailure = { error ->
                    Timber.e(error, "Failed to get current user")
                }
            )
        }
    }
    
    fun signUp(email: String, password: String, username: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            authRepository.signUp(email, password, username).fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Success(user)
                    Timber.d("Sign up successful")
                },
                onFailure = { error ->
                    _authState.value = AuthState.Error(error.message ?: "Sign up failed")
                    Timber.e(error, "Sign up failed")
                }
            )
        }
    }
    
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            authRepository.signIn(email, password).fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Success(user)
                    Timber.d("Sign in successful")
                },
                onFailure = { error ->
                    _authState.value = AuthState.Error(error.message ?: "Sign in failed")
                    Timber.e(error, "Sign in failed")
                }
            )
        }
    }
    
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut().fold(
                onSuccess = {
                    _currentUser.value = null
                    _authState.value = AuthState.Idle
                    Timber.d("Sign out successful")
                },
                onFailure = { error ->
                    Timber.e(error, "Sign out failed")
                }
            )
        }
    }
    
    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
    
    fun isUserLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }
}
