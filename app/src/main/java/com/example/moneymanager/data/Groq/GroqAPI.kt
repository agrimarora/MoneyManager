package com.example.moneymanager.data.Groq

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GroqApi {
    @Headers(
        "Authorization: Bearer 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",
        "Content-Type: application/json"
    )
    @POST("v1/chat/completions")
    fun getChatResponse(@Body request: GroqRequest): Call<GroqResponse>
}
