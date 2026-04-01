package com.example.moneymanager.domain.useCase

import com.example.moneymanager.common.ResultState
import com.example.moneymanager.common.model.GoalModel
import com.example.moneymanager.common.model.UserData
import com.example.moneymanager.domain.Repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddGoalUserCasee@Inject constructor(private val repo: Repo) {
    fun AddGoal(goal: GoalModel): Flow<ResultState<String>> {
        return repo.setGoals(goal)
    }

}