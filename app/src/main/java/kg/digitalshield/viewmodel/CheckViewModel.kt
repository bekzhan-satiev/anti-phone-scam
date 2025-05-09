package kg.digitalshield.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kg.digitalshield.dto.request.CheckRequest
import kg.digitalshield.service.CheckApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckViewModel @Inject constructor(
    private val checkApiService: CheckApiService
) : ViewModel() {
    private val _checkState = MutableStateFlow(CheckState())
    val checkState: StateFlow<CheckState> = _checkState

    fun isFraudNumber(checkRequest: CheckRequest) {
        viewModelScope.launch {
            _checkState.update { CheckState(isInProgress = true) }
            try {
                val response = checkApiService.checkForFraud(checkRequest)

                if (response.isSuccessful) {
                    val isFraud = response.body()
                    _checkState.update {
                        CheckState(isFraudNumber = isFraud)
                    }
                } else {
                    _checkState.update { CheckState(error = response.toString()) }
                }
            } catch (e: Exception) {
                _checkState.update { CheckState(error = e.message ?: "Unknown error") }
            }
        }
    }
}

data class CheckState(
    val isInProgress: Boolean = false,
    val isFraudNumber: Boolean? = null,
    val error: String? = null
)