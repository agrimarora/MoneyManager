package com.example.moneymanager.common.model

data class ExpenseModel(
    val description:String="",
    val category: String="",
    val amount : String="",
    val date: Long = System.currentTimeMillis(),
    val id:String=""
)
