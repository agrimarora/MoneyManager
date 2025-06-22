package com.example.moneymanager.domain.Groq

import com.example.moneymanager.data.Groq.GroqMessage
import com.example.moneymanager.data.Groq.GroqRequest
import com.example.moneymanager.data.Groq.GroqResponse
import com.example.moneymanager.data.Groq.RetrofitClient

import dagger.hilt.android.lifecycle.HiltViewModel

class ChatRepository {
    fun sendMessage(
        messages: List<GroqMessage>,
        onResult: (GroqMessage) -> Unit,
        onError: (String) -> Unit
    ) {
        val request = GroqRequest(messages = messages)

        RetrofitClient.api.getChatResponse(request).enqueue(object : retrofit2.Callback<GroqResponse> {
            override fun onResponse(
                call: retrofit2.Call<GroqResponse>,
                response: retrofit2.Response<GroqResponse>
            ) {
                if (response.isSuccessful) {
                    val message = response.body()?.choices?.firstOrNull()?.message
                    message?.let { onResult(it) } ?: onError("Empty response")
                } else {
                    onError("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<GroqResponse>, t: Throwable) {
                onError("Failure: ${t.message}")
            }
        })
    }
}
