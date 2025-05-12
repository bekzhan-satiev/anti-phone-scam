package kg.digitalshield.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kg.digitalshield.db.Call
import kg.digitalshield.db.CallRepository
import kg.digitalshield.dto.CallDetailState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(
    private val callRepository: CallRepository
) : ViewModel() {

    private val _state = MutableStateFlow<CallDetailState>(CallDetailState.Loading)
    val state: StateFlow<CallDetailState> = _state

    val calls = callRepository.getAllCalls()

    fun loadCallDetails(callId: Int) {
        viewModelScope.launch {
            _state.value = CallDetailState.Loading
            try {
                val call = callRepository.getById(callId)
                _state.value = if (call != null) {
                    CallDetailState.Success(call)
                } else {
                    CallDetailState.Error("Call not found")  // Fixed: Added missing assignment
                }
            } catch (e: Exception) {
                _state.value = CallDetailState.Error(e.message ?: "Unknown error")  // Added null check
            }
        }
    }

    fun resetState() {
        viewModelScope.launch {
            _state.value = CallDetailState.Loading
        }
    }
}