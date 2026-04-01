package com.example.moneymanager.domain.useCase

import com.example.moneymanager.common.ResultState
import com.example.moneymanager.domain.Repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteGoalUseCase@Inject constructor(private val repo: Repo) {
    fun DeleteGoalUseCase(goalId: String):Flow<ResultState<String>> {
        return repo.deleteGoal(goalId)
    }
}