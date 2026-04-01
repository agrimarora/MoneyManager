package com.example.moneymanager.Navigation

import kotlinx.serialization.Serializable

sealed class Routes {
@Serializable
object Login : Routes()
    @Serializable
    object Dashboard : Routes()
    @Serializable
    object SignUp : Routes()
    @Serializable
    object AddExpense : Routes()
    @Serializable
    object Report : Routes()
    @Serializable
    object Profile : Routes()
    @Serializable
    object Goal: Routes()

}