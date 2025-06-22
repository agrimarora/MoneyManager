package com.example.moneymanager.common.model

import coil3.Bitmap

data class ChatState (
    val chatlist:MutableList<ChatModel> = mutableListOf(),
    val prompt:String="",
    val bitmap: Bitmap?=null
)