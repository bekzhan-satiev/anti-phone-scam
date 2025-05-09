package kg.digitalshield.auth

import kg.digitalshield.service.AuthApiService
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val tokenRepository: TokenRepository,
    private val authApiService: AuthApiService
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            val refreshToken = tokenRepository.getRefreshToken()
            if (refreshToken.isNullOrEmpty()) return@runBlocking null

            try {
                val refreshResponse = authApiService.refreshToken(refreshToken)
                if (refreshResponse.isSuccessful) {
                    val newTokens = refreshResponse.body() ?: return@runBlocking null
                    tokenRepository.saveTokens(newTokens.accessToken, newTokens.refreshToken)
                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${newTokens.accessToken}")
                        .build()
                } else {
                    // Refresh failed — clear stored tokens
                    tokenRepository.clearTokens()
                    null
                }
            } catch (e: Exception) {
                // Network error or something else — optionally clear tokens
                tokenRepository.clearTokens()
                null
            }
        }
    }

}
