package com.example.moneymanager.domain.Repo

import com.example.moneymanager.common.ResultState
import com.example.moneymanager.common.model.ExpenseModel
import com.example.moneymanager.common.model.IncomeModel
import com.example.moneymanager.common.model.UserData
import com.example.moneymanager.common.model.UserDataParent
import kotlinx.coroutines.flow.Flow

interface Repo {
    fun registeruserwithemailandpassword(userdata: UserData):Flow<ResultState<String>>
    fun loginuserwithemailandpassword(userdata: UserData):Flow<ResultState<String>>
    fun AddExpense(expensedata: ExpenseModel):Flow<ResultState<String>>
    fun getuserbyUID(UID:String):Flow<ResultState<UserDataParent>>

    fun addIncomeToUser(userData: UserData): Flow<ResultState<String>>
    fun getallexpenditure():Flow<ResultState<List<ExpenseModel>>>
    fun updateUserData(userDataParent: UserDataParent): Flow<ResultState<String>>

}