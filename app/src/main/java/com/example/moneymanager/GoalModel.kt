package com.example.moneymanager

data class GoalModel(
    val target: String = "",
    val progres: String = "0",   // percentage
    val Date: String = "",
    val id: String = "",
    val amount: Double = 0.0,    // current saved amount
    val targetAmount: Double = 0.0, // total target
    val achieved: Boolean = false
)
