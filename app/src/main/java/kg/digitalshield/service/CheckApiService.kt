package kg.digitalshield.service

import kg.digitalshield.dto.request.CheckRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CheckApiService {

    @POST("search")
    suspend fun checkForFraud(@Body checkRequest: CheckRequest): Response<Boolean>

}