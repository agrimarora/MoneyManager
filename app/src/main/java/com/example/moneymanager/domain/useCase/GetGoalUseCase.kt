package com.example.moneymanager.domain.useCase

import com.example.moneymanager.common.ResultState
import com.example.moneymanager.common.model.GoalModel
import com.example.moneymanager.domain.Repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGoalUseCase@Inject constructor(val repo: Repo) {
    fun GetAllGoals(): Flow<ResultState<List<GoalModel>>>  {
        return repo.getGoals()

    }
}