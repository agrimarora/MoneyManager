package com.example.moneymanager.domain.useCase

import com.example.moneymanager.common.ResultState
import com.example.moneymanager.common.model.ExpenseModel
import com.example.moneymanager.domain.Repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddExpenseUseCase@Inject constructor(private val repo: Repo) {
    fun AddExpense(expensedata: ExpenseModel): Flow<ResultState<String>> {
        return repo.AddExpense(expensedata)
    }

    }

