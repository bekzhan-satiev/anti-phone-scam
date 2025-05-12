package kg.digitalshield.api

import kg.digitalshield.dto.request.CheckRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CheckApi {

    @POST("check")
    suspend fun checkForFraud(@Body checkRequest: CheckRequest): Response<Boolean>

}