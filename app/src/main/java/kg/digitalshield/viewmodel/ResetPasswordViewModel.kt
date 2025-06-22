package kg.digitalshield.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kg.digitalshield.api.ResetPasswordApi
import kg.digitalshield.dto.request.ResetPasswordRequest
import kg.digitalshield.state.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val resetPasswordApi: ResetPasswordApi
) : ViewModel() {
    private val _resetState = MutableStateFlow(RequestState())
    val resetState: StateFlow<RequestState> = _resetState

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _resetState.update { it.copy(isLoading = true) }

            try {
                val resetPasswordRequest = ResetPasswordRequest(email = email)
                val response = resetPasswordApi.resetPassword(resetPasswordRequest)
                if (response.isSuccessful) {
                    _resetState.update { RequestState(isSuccess = true) }
                } else {
                    _resetState.update { RequestState(error = response.body().toString()) }
                }
            } catch (e: Exception) {
                _resetState.update { RequestState(error = "Network error: ${e.message}") }
            }
        }
    }
}