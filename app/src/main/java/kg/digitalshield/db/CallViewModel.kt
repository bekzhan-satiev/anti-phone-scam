package kg.digitalshield.db

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun add(call: Call) {
        viewModelScope.launch(Dispatchers.IO) {
            callRepository.add(call)
        }
    }

    fun deleteById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            callRepository.deleteById(id)
        }
    }
}