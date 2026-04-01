package com.example.moneymanager.data.Groq

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GroqApi {
    @Headers(
        "Authorization: Bearer your_actual_key_here", // ✅ Replace with your actual key
        "Content-Type: application/json"
    )
    // ✅ Removed "v1/" here
    @POST("chat/completions")
    fun getChatResponse(@Body request: GroqRequest): Call<GroqResponse>
}