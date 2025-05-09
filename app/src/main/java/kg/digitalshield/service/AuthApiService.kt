package kg.digitalshield.service

import kg.digitalshield.dto.request.LoginRequest
import kg.digitalshield.dto.request.RegisterRequest
import kg.digitalshield.dto.response.LoginResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<ResponseBody>

    @POST("refresh")
    suspend fun refreshToken(@Body refreshToken: String): Response<LoginResponse>
}