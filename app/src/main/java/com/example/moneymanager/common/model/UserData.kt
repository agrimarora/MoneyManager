package com.example.moneymanager.common.model

data class UserData(


    val email: String="",
    val password: String="",
    val name: String="",
    val phoneNumber: String="",
    val income: String="",


)
data class UserDataParent(val nodeID: String?="", val Userdata: UserData?=UserData())