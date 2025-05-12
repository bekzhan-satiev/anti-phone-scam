package kg.digitalshield.api

import kg.digitalshield.dto.request.AnalyzeRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface  AnalyzeApi {

    @POST("analyze")
    suspend fun analyzePhrase(@Body analyzeRequest: AnalyzeRequest): Response<List<String>>

}