package kg.digitalshield.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kg.digitalshield.auth.AuthApiService
import kg.digitalshield.auth.LoginRequest
import kg.digitalshield.auth.LoginState
import kg.digitalshield.auth.TokenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenRepository: TokenRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.update { it.copy(isLoading = true) }

            try {
                val loginRequest = LoginRequest(username = username, password = password)
                val response = authApiService.login(loginRequest)

                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        tokenRepository.saveTokens(body.accessToken, body.refreshToken)
                        _loginState.update { LoginState(isSuccess = true) }
                    }
                } else {
                    _loginState.update { LoginState(error = "Invalid credentials") }
                }
            } catch (e: Exception) {
                _loginState.update { LoginState(error = "Network error: ${e.message}") }
            }
        }
    }

    fun resetState() {
        _loginState.update { LoginState() }
    }
}
