package com.example.moneymanager.presentation.Viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.common.ResultState
import com.example.moneymanager.common.model.ExpenseModel
import com.example.moneymanager.common.model.GoalModel
import com.example.moneymanager.common.model.UserData
import com.example.moneymanager.common.model.UserDataParent
import com.example.moneymanager.domain.useCase.AddExpenseUseCase
import com.example.moneymanager.domain.useCase.AddGoalUserCasee
import com.example.moneymanager.domain.useCase.AddIncome
import com.example.moneymanager.domain.useCase.CreateuserUseCase
import com.example.moneymanager.domain.useCase.DeleteGoalUseCase
import com.example.moneymanager.domain.useCase.EditGoalUseCase
import com.example.moneymanager.domain.useCase.GetAllExpensesUseCase
import com.example.moneymanager.domain.useCase.GetGoalUseCase
import com.example.moneymanager.domain.useCase.GetUserDetailsUseCase
import com.example.moneymanager.domain.useCase.LogInUser
import com.example.moneymanager.domain.useCase.UpdateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    val createuserUseCase: CreateuserUseCase,
    val logInUser: LogInUser,
    val addExpenseUseCase: AddExpenseUseCase,
    val addIncome: AddIncome,
    val getUserDetailsUseCase: GetUserDetailsUseCase,
    val getAllExpensesUseCase:GetAllExpensesUseCase,
    val updateuserUseCase: UpdateUserUseCase,

    val goalUserCasee: AddGoalUserCasee,
    val deleteGoalUseCase: DeleteGoalUseCase,
    val editGoalUseCase: EditGoalUseCase,
    val getGoalUseCase: GetGoalUseCase


) : ViewModel() {
    private val _signupScreenstate = mutableStateOf(SignUpScreenSate())
    val signupScreenstate = _signupScreenstate
    private val _addExpenseScreenstate = mutableStateOf(AddExpenseScreenSate())
    val addExpenseScreenstate = _addExpenseScreenstate
    private val _logInScreenstate = mutableStateOf(LogINScreenSate())
    val logINScreenstate = _logInScreenstate
    private val _dashboardScreenstate = mutableStateOf(DashboardScreenSate())
    val dashboardScreenstate = _dashboardScreenstate
    private val _expenditureListScreenstate = mutableStateOf(ExpenditureListScreenSate())
    val expenditureListScreenstate = _expenditureListScreenstate
    private val _ProfileScreenstate: MutableState<ProfileScreenState> = mutableStateOf(ProfileScreenState())
    val ProfileScreenState = _ProfileScreenstate
    private val _GoalScreenstate: MutableState<AddGoalScreenSate> = mutableStateOf(AddGoalScreenSate())
    val GoalScreenState = _GoalScreenstate



    init {
        getAllExpenditure()

    }

    fun LoginUser(userdata: UserData) {
        viewModelScope.launch {
            logInUser.loginuser(userdata).collect { result ->
                when (result) {
                    ResultState.Loading -> {
                        _logInScreenstate.value = LogINScreenSate(isLoading = true)
                    }
                    is ResultState.Succes -> {
                        _logInScreenstate.value = LogINScreenSate(
                            success = true, userdata = result.data
                        )


                    }

                    is ResultState.error -> {
                        _logInScreenstate.value = LogINScreenSate(error = result.message)
                    }
                }


            }
        }
    }

    fun getuserbyUID(uid: String) {
        viewModelScope.launch {
            getUserDetailsUseCase.getUserBYUid(uid).collect { result ->

                when (result) {
                    ResultState.Loading -> {
                        _dashboardScreenstate.value = DashboardScreenSate(isLoading = true)
                    }

                    is ResultState.Succes -> {
                        Log.d(
                            "getuserbyUID ", "${result.data}"
                        )
                        _dashboardScreenstate.value = DashboardScreenSate(
                            success = true, userdata = result.data
                        )
                        _ProfileScreenstate.value = ProfileScreenState(
                        success = true,
                            userdata = result.data
                        )

                    }

                    is ResultState.error -> {
                        _dashboardScreenstate.value = DashboardScreenSate(error = result.message)

                    }
                }
            }
        }
    }


    fun createUser(userdata: UserData) {
        viewModelScope.launch {
            createuserUseCase.Createuser(userdata).collect { result ->
                when (result) {
                    ResultState.Loading -> {
                        _signupScreenstate.value = SignUpScreenSate(isLoading = true)
                    }

                    is ResultState.Succes -> {
                        _signupScreenstate.value = SignUpScreenSate(
                            success = true, userdata = result.data
                        ) // Update success correctly
                    }

                    is ResultState.error -> {
                        _signupScreenstate.value = SignUpScreenSate(error = result.message)
                    }
                }
            }
        }
    }
    fun updateuser(userDataParent: UserDataParent) {
        viewModelScope.launch {
            updateuserUseCase.UpdateUserUseCase(userDataParent).collect { result ->
                when (result) {
                    ResultState.Loading -> {
                        _ProfileScreenstate.value = ProfileScreenState(isLoading = true)
                    }
                    is ResultState.Succes->{
                        _ProfileScreenstate.value = ProfileScreenState(
                            success = true


                        )

                    }
                    is ResultState.error -> {
                        _ProfileScreenstate.value = ProfileScreenState(error = result.message)


                    }
                }

        }

    }}

    fun AddExpense(expensedata: ExpenseModel) {
        viewModelScope.launch {
            addExpenseUseCase.AddExpense(expensedata).collect { result ->
                when (result) {
                    ResultState.Loading -> {
                        _addExpenseScreenstate.value = AddExpenseScreenSate(isLoading = true)
                    }

                    is ResultState.Succes<*> -> {
                        _addExpenseScreenstate.value = AddExpenseScreenSate(
                            success = true,
                            )
                        getAllExpenditure()
                    }

                    is ResultState.error<*> -> {

                        _addExpenseScreenstate.value =
                            AddExpenseScreenSate(error = result.message)

                    }
                }


            }

        }
    }
    fun deleteGoal(goalId: String) {
        viewModelScope.launch {
            deleteGoalUseCase.DeleteGoalUseCase(goalId).collect { result ->
                when (result) {
                    ResultState.Loading -> {
                        _GoalScreenstate.value = AddGoalScreenSate(isLoading = true)
                    }
                    is ResultState.Succes<*> -> {
                        _GoalScreenstate.value = AddGoalScreenSate(success = true)
                        getGoals()
                    }

                     is ResultState.error<*> -> {
                        _GoalScreenstate.value = AddGoalScreenSate(error = result.message)
                    }
                }
            }
        }
    }
    fun editGoal(goal: GoalModel) {
        viewModelScope.launch {
            editGoalUseCase.EditGoalUseCase(goal).collect { result ->
                when (result) {
                    ResultState.Loading -> {
                        _GoalScreenstate.value = AddGoalScreenSate(isLoading = true)
                    }
                    is ResultState.Succes<*> -> {
                        _GoalScreenstate.value = AddGoalScreenSate(success = true)
                        getGoals()
                    }
                    is ResultState.error<*> -> {
                        _GoalScreenstate.value = AddGoalScreenSate(error = result.message)
                    }
                }
            }
        }
    }
    fun getGoals() {
        viewModelScope.launch {
            getGoalUseCase.GetAllGoals().collect { result ->
                when (result) {
                    ResultState.Loading -> {
                        _GoalScreenstate.value = AddGoalScreenSate(isLoading = true)
                    }
                    is ResultState.Succes -> {
                        _GoalScreenstate.value = AddGoalScreenSate(
                            success = true,
                            goalList = result.data
                        )
                    }
                    is ResultState.error -> {
                        _GoalScreenstate.value = AddGoalScreenSate(
                            error = result.message
                        )
                    }
                }
            }
        }
    }




    fun AddGoal(goal: GoalModel) {
        viewModelScope.launch {
            goalUserCasee.AddGoal(goal).collect { result ->
                when (result) {
                    ResultState.Loading -> {
                        _GoalScreenstate.value = AddGoalScreenSate(isLoading = true)
                    }
                    is ResultState.Succes<*> -> {
                        _GoalScreenstate.value = AddGoalScreenSate(success = true)
                        getGoals()
                    }
                    is ResultState.error<*> -> {
                        _GoalScreenstate.value = AddGoalScreenSate(error = result.message)
                    }
                }
            }
        }
    }

    fun AddIncome(userData: UserData) {
        viewModelScope.launch {
            addIncome.AddIncome(userData).collect { result ->
                when (result) {
                    ResultState.Loading -> {
                        _dashboardScreenstate.value = DashboardScreenSate(isLoading = true)

                    }

                    is ResultState.Succes<*> -> {
                        _dashboardScreenstate.value = DashboardScreenSate(
                            success = true,
                        )
                    }

                    is ResultState.error<*> -> {
                        _dashboardScreenstate.value =
                            DashboardScreenSate(error = result.message)

                    }
                }
            }
        }

    }

    fun getAllExpenditure() {
        viewModelScope.launch {
            getAllExpensesUseCase.getAllExpenses().collect { result ->
                when (result) {
                    ResultState.Loading -> {
                        _expenditureListScreenstate.value=ExpenditureListScreenSate(isLoading = true)
                    }

                    is ResultState.Succes -> {
                        Log.d("GetAllExpenditureFunction", "getAllExpenditure: ${result.data}")
                        _expenditureListScreenstate.value=ExpenditureListScreenSate(
                            success = true,
                            expenses = result.data
                        )

                    }
                    is ResultState.error -> {
                        _expenditureListScreenstate.value=ExpenditureListScreenSate(error = result.message)
                    }
                }


            }
        }
    }
}

data class SignUpScreenSate(
    val isLoading: Boolean = false,
    val error: String? = null,
    val userdata: String? = null,
    val success: Boolean? = false
)

data class LogINScreenSate(
    val isLoading: Boolean = false,
    val error: String? = null,
    val userdata: String? = null,
    val success: Boolean? = false
)

data class AddExpenseScreenSate(
    val isLoading: Boolean = false,
    val error: String? = null,
    val userdata: String? = null,
    val success: Boolean? = false
)
data class AddGoalScreenSate(
    val isLoading: Boolean = false,
    val error: String? = null,
    val goalList: List<GoalModel>? = null,
    val success: Boolean? = false
)

data class DashboardScreenSate(
    val isLoading: Boolean = false,
    val error: String? = null,
    val userdata: UserDataParent? = null,
    val success: Boolean? = false
)

data class ProfileScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val userdata: UserDataParent? = null,
    val success: Boolean? = false
)
data class ExpenditureListScreenSate(
    val isLoading: Boolean = false,
    val error: String? = null,
    val expenses: List<ExpenseModel>? = emptyList(),
    val success: Boolean? = false
)