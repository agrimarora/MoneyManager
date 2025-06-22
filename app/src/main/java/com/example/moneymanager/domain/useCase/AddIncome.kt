package com.example.moneymanager.domain.useCase

import com.example.moneymanager.common.ResultState
import com.example.moneymanager.common.model.ExpenseModel
import com.example.moneymanager.common.model.UserData
import com.example.moneymanager.domain.Repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddIncome@Inject constructor(private val repo: Repo) {
    fun AddIncome(userData: UserData): Flow<ResultState<String>> {
        return repo.addIncomeToUser(userData)
    }

}