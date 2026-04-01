package com.example.moneymanager.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.moneymanager.R
import com.example.moneymanager.common.model.GoalModel
import com.example.moneymanager.presentation.Viewmodel.AppViewModel
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@Composable
fun GoalScreen(navController: NavHostController, viewModel: AppViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val state = viewModel.GoalScreenState.value
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var showAddAmountDialog by remember { mutableStateOf(false) }
    var selectedGoal by remember { mutableStateOf<GoalModel?>(null) }

    LaunchedEffect(true) {
        viewModel.getGoals()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = colorResource(id = R.color.prussian_Blue))
                }
            }

            !state.error.isNullOrEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Something went wrong!\n${state.error}")
                }
            }

            else -> {
                val goals = state.goalList ?: emptyList()
                val sortedGoals = goals.sortedBy { it.achieved } // incomplete first, achieved last

                if (sortedGoals.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(sortedGoals) { goal ->
                            GoalCard(
                                goal = goal,
                                onEdit = {
                                    selectedGoal = goal
                                    showAddGoalDialog = true
                                },
                                onDelete = {
                                    viewModel.deleteGoal(goal.id)
                                    Toast.makeText(context, "Goal deleted", Toast.LENGTH_SHORT).show()
                                    viewModel.getGoals()
                                },
                                onAddAmount = {
                                    selectedGoal = goal
                                    showAddAmountDialog = true
                                },
                                viewModel = viewModel
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No goals added yet!",
                            color = colorResource(id = R.color.prussian_Blue)
                        )
                    }
                }

                FloatingActionButton(
                    onClick = {
                        selectedGoal = null
                        showAddGoalDialog = true
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = colorResource(id = R.color.prussian_Blue)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Goal",
                        tint = Color.White
                    )
                }

                // Add/Edit Goal Dialog
                if (showAddGoalDialog) {
                    AddGoalDialog(
                        onDismiss = { showAddGoalDialog = false },
                        viewModel = viewModel,
                        navController = navController,
                        goalToEdit = selectedGoal
                    )
                }

                // Add Amount Dialog
                if (showAddAmountDialog && selectedGoal != null) {
                    AddAmountDialog(
                        goal = selectedGoal!!,
                        onDismiss = { showAddAmountDialog = false },
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun GoalCard(
    goal: GoalModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAddAmount: (GoalModel) -> Unit,
    viewModel: AppViewModel
) {
    var showMenu by remember { mutableStateOf(false) }
    val progressPercent = goal.progres.toFloatOrNull() ?: 0f

    Surface(
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 6.dp,
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = goal.target,
                    fontSize = 20.sp,
                    color = colorResource(id = R.color.prussian_Blue)
                )

                Box {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = colorResource(id = R.color.prussian_Blue)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                showMenu = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                showMenu = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Target Amount: ₹${goal.targetAmount}",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Text(
                text = "Saved: ₹${goal.amount}",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Text(
                text = "Target Date: ${goal.Date}",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = (progressPercent / 100f).coerceIn(0f, 1f),
                color = colorResource(id = R.color.prussian_Blue),
                trackColor = Color.LightGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Progress: ${progressPercent.toInt()}%",
                color = colorResource(id = R.color.prussian_Blue),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { onAddAmount(goal) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.prussian_Blue)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = "Add Amount", color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalDialog(
    onDismiss: () -> Unit,
    viewModel: AppViewModel,
    navController: NavController,
    goalToEdit: GoalModel? = null
) {
    var target by remember { mutableStateOf(goalToEdit?.target ?: "") }
    var amount by remember { mutableStateOf(goalToEdit?.targetAmount?.toString() ?: "") }
    val calendarState = rememberSheetState()
    val selectedDateMillis = remember {
        mutableStateOf(goalToEdit?.Date?.toLongOrNull())
    }
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val context = LocalContext.current

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (goalToEdit == null) "Add Goal" else "Edit Goal",
                    color = colorResource(id = R.color.prussian_Blue),
                    fontSize = 25.sp
                )

                Spacer(modifier = Modifier.height(30.dp))

                OutlinedTextField(
                    value = target,
                    onValueChange = { target = it },
                    label = { Text("Description") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorResource(id = R.color.prussian_Blue),
                        unfocusedBorderColor = colorResource(id = R.color.prussian_Blue)
                    )
                )

                Spacer(modifier = Modifier.height(30.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Target Amount in Rupees") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorResource(id = R.color.prussian_Blue),
                        unfocusedBorderColor = colorResource(id = R.color.prussian_Blue)
                    )
                )

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = { calendarState.show() },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.prussian_Blue))
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = selectedDateMillis.value?.let { dateFormatter.format(Date(it)) } ?: "Select Date",
                        color = Color.White
                    )
                }

                CalendarDialog(
                    state = calendarState,
                    config = CalendarConfig(
                        yearSelection = true,
                        monthSelection = true,
                        style = CalendarStyle.MONTH,
                        minYear = 2025,
                        maxYear = 2035
                    ),
                    selection = CalendarSelection.Date { localDate ->
                        selectedDateMillis.value = localDate
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant().toEpochMilli()
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (target.isBlank() || amount.isBlank() || amount.toDoubleOrNull() == null || selectedDateMillis.value == null) {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val goal = GoalModel(
                            id = goalToEdit?.id ?: UUID.randomUUID().toString(),
                            target = target,
                            targetAmount = amount.toDouble(),
                            amount = goalToEdit?.amount ?: 0.0,
                            progres = goalToEdit?.progres ?: "0",
                            Date = selectedDateMillis.value.toString(),
                            achieved = goalToEdit?.achieved ?: false
                        )

                        if (goalToEdit == null) {
                            viewModel.AddGoal(goal)
                            Toast.makeText(context, "Goal added", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.editGoal(goal)
                            Toast.makeText(context, "Goal updated", Toast.LENGTH_SHORT).show()
                        }

                        viewModel.getGoals()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.prussian_Blue))
                ) {
                    Text(if (goalToEdit == null) "Add" else "Update")
                }
            }
        }
    }
}

@Composable
fun AddAmountDialog(goal: GoalModel, onDismiss: () -> Unit, viewModel: AppViewModel) {
    var addAmount by remember { mutableStateOf("") }
    val context = LocalContext.current

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Add Amount",
                    color = colorResource(id = R.color.prussian_Blue),
                    fontSize = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = addAmount,
                    onValueChange = { addAmount = it },
                    label = { Text("Enter amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val added = addAmount.toDoubleOrNull()
                        if (added == null || added <= 0) {
                            Toast.makeText(context, "Enter valid amount", Toast.LENGTH_SHORT).show()
                        } else {
                            val newAmount = (goal.amount + added).coerceAtMost(goal.targetAmount)
                            val newProgress = ((newAmount / goal.targetAmount) * 100).toInt()
                            val updatedGoal = goal.copy(
                                amount = newAmount,
                                progres = newProgress.toString(),
                                achieved = newProgress >= 100
                            )
                            viewModel.editGoal(updatedGoal)
                            viewModel.getGoals()
                            Toast.makeText(context, "Amount added", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.prussian_Blue))
                ) {
                    Text("Add", color = Color.White)
                }
            }
        }
    }
}
