package com.example.moneymanager.data.Groq


data class GroqMessage(val role: String, val content: String)

data class GroqRequest(
    val model: String = "llama3-8b-8192",
    val messages: List<GroqMessage>,
    val temperature: Double = 0.7
)

data class GroqResponse(val choices: List<Choice>)
data class Choice(val message: GroqMessage)
