package com.example.moneymanager.domain.useCase

import com.example.moneymanager.common.ResultState
import com.example.moneymanager.common.model.ExpenseModel
import com.example.moneymanager.domain.Repo.Repo

import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class GetExpenditurewithdaterang@Inject constructor(private val repo: Repo) {
    suspend fun getExpenditureByDateRange(startDate: Date, endDate: Date): Flow<ResultState<List<ExpenseModel>>> {
        return repo.getExpenditureByDateRange(startDate = startDate , endDate =endDate )
    }

}

