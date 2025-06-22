package com.example.moneymanager.domain.useCase

import com.example.moneymanager.common.ResultState
import com.example.moneymanager.common.model.ExpenseModel
import com.example.moneymanager.domain.Repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllExpensesUseCase@Inject constructor(private val repo: Repo) {
suspend fun getAllExpenses(): Flow<ResultState<List<ExpenseModel>>> {
    return repo.getallexpenditure()
}

}

