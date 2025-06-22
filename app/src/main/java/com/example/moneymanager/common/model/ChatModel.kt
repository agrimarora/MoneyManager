package com.example.moneymanager.common.model

import coil3.Bitmap

data class ChatModel (
    val prompt: String,
    val bitmap: Bitmap?,
    val IsFromUser: Boolean
)