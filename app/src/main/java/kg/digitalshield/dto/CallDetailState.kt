package kg.digitalshield.dto

import kg.digitalshield.db.Call

sealed class CallDetailState {
    object Loading : CallDetailState()
    data class Success(val call: Call) : CallDetailState()
    data class Error(val message: String? = null) : CallDetailState()
}