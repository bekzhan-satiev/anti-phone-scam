package kg.digitalshield.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kg.digitalshield.db.Call
import kg.digitalshield.db.CallRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(
    private val callRepository: CallRepository
) : ViewModel() {

    val calls = callRepository.getAllCalls()

    var selectedCall by mutableStateOf<Call?>(null)

    fun updateSelectedCall(call: Call) {
        selectedCall = call
    }

    fun save(call: Call) {
        viewModelScope.launch(Dispatchers.IO) {
            callRepository.save(call)
        }
    }

}