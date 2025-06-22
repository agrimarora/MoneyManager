package com.example.moneymanager.domain.useCase

import com.example.moneymanager.common.ResultState
import com.example.moneymanager.common.model.UserDataParent
import com.example.moneymanager.domain.Repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserDetailsUseCase @Inject constructor(private val repo: Repo) {
    fun getUserBYUid(uid: String) :Flow<ResultState<UserDataParent>>
    {
        return repo.getuserbyUID(uid)
    }


}