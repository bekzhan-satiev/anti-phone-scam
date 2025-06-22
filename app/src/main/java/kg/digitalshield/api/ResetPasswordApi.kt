package kg.digitalshield.api

import kg.digitalshield.dto.request.ResetPasswordRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ResetPasswordApi {

    @POST
    suspend fun resetPassword(@Body resetPasswordRequest: ResetPasswordRequest): Response<Boolean>

}