package kg.digitalshield.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kg.digitalshield.auth.AuthApiService
import kg.digitalshield.auth.LoginRequest
import kg.digitalshield.auth.RegisterRequest
import kg.digitalshield.auth.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authApiService: AuthApiService
) : ViewModel() {
    private val _registerState = MutableStateFlow(RequestState())
    val registerState: StateFlow<RequestState> = _registerState

    fun register(phoneNumber: String, password: String) {
        viewModelScope.launch {
            _registerState.update { it.copy(isLoading = true) }

            try {
                val registerRequest = RegisterRequest(phoneNumber = phoneNumber, password = password)
                val response = authApiService.register(registerRequest)

                if (response.isSuccessful) {
                        _registerState.update { RequestState(isSuccess = true) }
                } else {
                    _registerState.update { RequestState(error = response.body().toString()) }
                }
            } catch (e: Exception) {
                _registerState.update { RequestState(error = "Network error: ${e.message}") }
            }
        }
    }

    fun resetState() {
        _registerState.update { RequestState() }
    }

}