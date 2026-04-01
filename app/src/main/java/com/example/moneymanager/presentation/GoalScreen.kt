package com.example.moneymanager.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.moneymanager.Navigation.Routes
import com.example.moneymanager.R
import com.example.moneymanager.common.model.GoalModel
import com.example.moneymanager.presentation.Viewmodel.AppViewModel
import com.example.moneymanager.ui.theme.transparentTextFieldColors
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreen(navController: NavHostController, viewModel: AppViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val state by viewModel.GoalScreenState
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var showAddAmountDialog by remember { mutableStateOf(false) }
    var selectedGoal by remember { mutableStateOf<GoalModel?>(null) }

    LaunchedEffect(true) {
        viewModel.getGoals()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Goals",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedGoal = null
                    showAddGoalDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Goal", tint = Color(0xFF084C00))
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                !state.error.isNullOrEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Something went wrong!\n${state.error}", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }

                else -> {
                    val goals = state.goalList ?: emptyList()
                    val sortedGoals = goals.sortedBy { it.achieved }

                    if (sortedGoals.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
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
                                    },
                                    onAddAmount = {
                                        selectedGoal = goal
                                        showAddAmountDialog = true
                                    }
                                )
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "No active goals found.\nAdd your first target.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            if (showAddGoalDialog) {
                AddGoalDialog(
                    onDismiss = { showAddGoalDialog = false },
                    viewModel = viewModel,
                    goalToEdit = selectedGoal
                )
            }

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

@Composable
fun GoalCard(
    goal: GoalModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAddAmount: (GoalModel) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val progressPercent = (goal.progres.toFloatOrNull() ?: 0f) / 100f
    val formattedDate = try {
        val date = Date(goal.Date.toLong())
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)
    } catch (e: Exception) {
        goal.Date
    }

    Surface(
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = goal.target,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Box {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(text = { Text("Edit") }, onClick = { showMenu = false; onEdit() })
                        DropdownMenuItem(text = { Text("Delete") }, onClick = { showMenu = false; onDelete() })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Saved", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("₹${"%,.0f".format(goal.amount)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Target", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("₹${"%,.0f".format(goal.targetAmount)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stitch Styled Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressPercent.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Deadline: $formattedDate",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${(progressPercent * 100).toInt()}% Complete",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onAddAmount(goal) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Text("ADD AMOUNT", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalDialog(
    onDismiss: () -> Unit,
    viewModel: AppViewModel,
    goalToEdit: GoalModel? = null
) {
    var target by remember { mutableStateOf(goalToEdit?.target ?: "") }
    var amount by remember { mutableStateOf(goalToEdit?.targetAmount?.toString() ?: "") }
    val calendarState = rememberSheetState()
    val selectedDateMillis = remember { mutableStateOf(goalToEdit?.Date?.toLongOrNull() ?: System.currentTimeMillis()) }
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val context = LocalContext.current

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (goalToEdit == null) "New Goal" else "Edit Goal",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = target,
                    onValueChange = { target = it },
                    label = { Text("Goal Description") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = transparentTextFieldColors()
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Target Amount (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = transparentTextFieldColors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { calendarState.show() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = dateFormatter.format(Date(selectedDateMillis.value)))
                }

                CalendarDialog(
                    state = calendarState,
                    config = CalendarConfig(style = CalendarStyle.MONTH),
                    selection = CalendarSelection.Date { localDate ->
                        selectedDateMillis.value = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (target.isBlank() || amount.isBlank() || amount.toDoubleOrNull() == null) {
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
                        } else {
                            viewModel.editGoal(goal)
                        }
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(if (goalToEdit == null) "ADD GOAL" else "UPDATE GOAL", fontWeight = FontWeight.Bold)
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
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Add Installment", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = addAmount,
                    onValueChange = { addAmount = it },
                    label = { Text("Enter Amount (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = transparentTextFieldColors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(32.dp))

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
                            
                            // Add to Expenditure with "Goal" category
                            com.example.moneymanager.common.model.ExpenseModel(
                                description = "Goal: ${goal.target}",
                                amount = added.toString(),
                                category = "Goal",
                                date = System.currentTimeMillis()
                            ).let { expense ->
                                viewModel.AddExpense(expense)
                            }
                            
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("ADD INSTALLMENT", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
