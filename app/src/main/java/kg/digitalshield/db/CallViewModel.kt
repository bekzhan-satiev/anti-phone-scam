package kg.digitalshield.db

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CallViewModel: ViewModel() {

    val callDao = MainApplication.callDatabase.getCallDao()

    val calls : LiveData<List<Call>> = callDao.getCallsOrderedByTime()

    var selectedCall by mutableStateOf<Call?>(null)
    fun updateSelectedCall(call: Call) {
        selectedCall = call
    }

    fun add(call: Call) {
        viewModelScope.launch(Dispatchers.IO) {
            callDao.save(call)
        }
    }

    fun deleteById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            callDao.delete(id)
        }
    }


}