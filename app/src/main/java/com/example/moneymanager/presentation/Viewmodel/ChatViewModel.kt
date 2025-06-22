package com.example.moneymanager.presentation.Viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.moneymanager.common.model.ExpenseModel
import com.example.moneymanager.common.model.UserData
import com.example.moneymanager.data.Groq.GroqMessage
import com.example.moneymanager.domain.Groq.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel


class ChatViewModel : ViewModel() {
    private val repository = ChatRepository()

    private val _messages = mutableStateListOf<GroqMessage>()
    val messages: List<GroqMessage> get() = _messages
    private val _reportMessages = mutableStateListOf<GroqMessage>()
    val reportMessages: List<GroqMessage> get() = _reportMessages

    fun sendMessage(userInput: String) {
        _messages.add(GroqMessage("user", userInput))

        repository.sendMessage(_messages, { reply ->
            _messages.add(reply)
        }, { error ->
            _messages.add(GroqMessage("assistant", error))
        })
    }
    fun sendReportMessage(userInput: UserData,epenses: List<ExpenseModel>) {
        _reportMessages.add(GroqMessage("user", "generate financial report with main expenditure and suggestion to save money for  $userInput and expenses is ${epenses} strictly stick to the data provided amount is in rupees"))

        repository.sendMessage(_reportMessages, { reply ->
            _reportMessages.add(reply)
            Log.d("Reply", "sendReportMessage:${reply} ")
        }, { error ->
            _reportMessages.add(GroqMessage("assistant", error))
        })
    }
}

