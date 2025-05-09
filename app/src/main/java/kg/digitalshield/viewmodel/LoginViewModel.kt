package kg.digitalshield.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kg.digitalshield.auth.TokenRepository
import kg.digitalshield.dto.request.LoginRequest
import kg.digitalshield.service.AuthApiService
import kg.digitalshield.state.RequestState
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

    private val _requestState = MutableStateFlow(RequestState())
    val requestState: StateFlow<RequestState> = _requestState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _requestState.update { RequestState(isLoading = true) }

            try {
                val loginRequest = LoginRequest(phoneNumber = username, password = password)
                val response = authApiService.login(loginRequest)

                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        tokenRepository.saveTokens(body.accessToken, body.refreshToken)
                        _requestState.update { RequestState(isSuccess = true) }
                    }
                } else {
                    _requestState.update { RequestState(error = "Invalid credentials") }
                    tokenRepository.clearTokens()
                }
            } catch (e: Exception) {
                _requestState.update { RequestState(error = "Network error: ${e.message}") }
            }
        }
    }

    fun resetState() {
        _requestState.update { RequestState() }
    }
}
