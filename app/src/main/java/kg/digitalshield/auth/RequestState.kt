package kg.digitalshield.auth

data class RequestState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)