package kg.digitalshield.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.jwt.JWT
import com.auth0.jwt.exceptions.JWTDecodeException
import dagger.hilt.android.lifecycle.HiltViewModel
import kg.digitalshield.auth.TokenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject
constructor(private val tokenRepository: TokenRepository) : ViewModel() {
    private val _phoneNumber = MutableStateFlow<String?>(null)
    val phoneNumber: StateFlow<String?> = _phoneNumber.asStateFlow()

    fun loadPhoneNumber() {
        if (_phoneNumber.value != null) return
        viewModelScope.launch {
//            val token = tokenRepository.getAccessToken()
//            val decodedPhone = token?.let { decodeSubFromToken(it) }
            _phoneNumber.value = "996500011737"
        }
    }

    private fun decodeSubFromToken(token: String): String? {
        return try {
            JWT.decode(token).subject?.takeIf { it.isNotBlank() }
        } catch (e: JWTDecodeException) {
            null
        }
    }

    fun clearData() {
        _phoneNumber.value = null
    }
}