package com.example.moneymanager.common.model



data class GoalModel(
    val id: String = "",
    val target: String = "",        // Goal description
    val targetAmount: Double = 0.0, // Total target amount
    val amount: Double = 0.0,      // Amount saved so far
    val progres: String = "0",      // Progress percentage as String
    val Date: String = "",          // Target date in millis as String
    val achieved: Boolean = false   // True if goal is completed
)

data class GoalDataParent(val nodeID: String?="", val Goaldata: GoalModel?= GoalModel())