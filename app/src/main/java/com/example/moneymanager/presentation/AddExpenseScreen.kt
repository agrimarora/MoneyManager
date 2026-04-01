package com.example.moneymanager.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.*
import com.example.moneymanager.Navigation.Routes
import com.example.moneymanager.R
import com.example.moneymanager.common.model.ExpenseModel
import com.example.moneymanager.presentation.Viewmodel.AppViewModel
import com.example.moneymanager.ui.theme.transparentTextFieldColors
import com.google.firebase.auth.FirebaseAuth
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    viewmodel: AppViewModel = hiltViewModel(),
    navController: NavController,
    firebaseAuth: FirebaseAuth
) {
    val state by viewmodel.addExpenseScreenstate
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }
    val filterOptions = listOf("All", "Last Week", "15 Days", "This Month")

    val expenseState by viewmodel.expenditureListScreenstate
    var expenses = (expenseState.expenses ?: emptyList()).sortedBy { it.date }

    val now = LocalDate.now()
    val filteredExpenses = expenses.filter { expense ->
        val expenseDate = LocalDate.ofEpochDay(expense.date / (1000 * 60 * 60 * 24))
        when (selectedFilter) {
            "Last Week" -> expenseDate.isAfter(now.minusWeeks(1))
            "15 Days" -> expenseDate.isAfter(now.minusDays(15))
            "This Month" -> expenseDate.month == now.month && expenseDate.year == now.year
            else -> true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {
        // App Bar / Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Spending Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
            IconButton(onClick = { /* More options */ }) {
                Icon(Icons.Default.MoreHoriz, contentDescription = "More")
            }
        }

        // Time Period Navigation
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filterOptions.forEach { option ->
                val isSelected = selectedFilter == option
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedFilter = option },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerLow,
                    border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null
                ) {
                    Text(
                        text = option,
                        modifier = Modifier.padding(vertical = 8.dp),
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Chart Section
        Surface(
            modifier = Modifier.fillMaxWidth().height(280.dp),
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (filteredExpenses.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No data available for this period", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    val pointsData = filteredExpenses.mapIndexed { index, expense ->
                        Point(index.toFloat(), expense.amount.toFloat())
                    }
                    val lineChartData = createLineChartData(pointsData, filteredExpenses, MaterialTheme.colorScheme.primary)
                    LineChart(
                        modifier = Modifier.fillMaxSize(),
                        lineChartData = lineChartData
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Ledger Title
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Activity Feed", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("${filteredExpenses.size} items", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Expense List
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            items(filteredExpenses.reversed()) { expense ->
                TransactionItem(expense) // Reusing TransactionItem from HomeScreen logic
            }
        }
    }

    // Add FAB
    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 32.dp, end = 20.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(20.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Expense", tint = Color(0xFF084C00))
        }
    }

    if (showAddDialog) {
        AddExpenseDialog(onDismiss = { showAddDialog = false }, firebaseAuth = firebaseAuth)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(
    viewmodel: AppViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    firebaseAuth: FirebaseAuth
) {
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Need", "Want")
    var selectedOption by remember { mutableStateOf(options[0]) }
    val user = firebaseAuth.currentUser

    val calendarState = rememberSheetState()
    val selectedDateMillis = remember { mutableStateOf<Long?>(System.currentTimeMillis()) }
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Add New Expense", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = transparentTextFieldColors()
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = transparentTextFieldColors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Category Selection
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    options.forEach { option ->
                        val isSelected = selectedOption == option
                        Surface(
                            modifier = Modifier.weight(1f).clickable { selectedOption = option },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Text(
                                text = option,
                                modifier = Modifier.padding(vertical = 12.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                // Date Picker Button
                OutlinedButton(
                    onClick = { calendarState.show() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = selectedDateMillis.value?.let { dateFormatter.format(Date(it)) } ?: "Select Date")
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
                        if (user != null && selectedDateMillis.value != null && description.isNotBlank()) {
                            viewmodel.AddExpense(
                                ExpenseModel(
                                    description = description,
                                    category = selectedOption,
                                    amount = amount,
                                    date = selectedDateMillis.value!!
                                )
                            )
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("ADD EXPENSE", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun createLineChartData(points: List<Point>, expenses: List<ExpenseModel>, primaryColor: Color): LineChartData {
    val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
    val xAxisData = AxisData.Builder()
        .axisStepSize(100.dp)
        .steps(points.size - 1)
        .labelData { i -> expenses.getOrNull(i)?.let { dateFormat.format(Date(it.date)) } ?: "" }
        .labelAndAxisLinePadding(15.dp)
        .axisLineColor(primaryColor.copy(alpha = 0.5f))
        .axisLabelColor(primaryColor)
        .build()

    val yAxisData = AxisData.Builder()
        .steps(5)
        .labelAndAxisLinePadding(20.dp)
        .labelData { i -> (i * (points.maxOfOrNull { it.y } ?: 1000f) / 5).toInt().toString() }
        .axisLineColor(primaryColor.copy(alpha = 0.5f))
        .axisLabelColor(primaryColor)
        .build()

    return LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = points,
                    LineStyle(color = primaryColor, width = 4f),
                    IntersectionPoint(color = primaryColor),
                    SelectionHighlightPoint(color = primaryColor),
                    ShadowUnderLine(
                        alpha = 0.1f,
                        brush = Brush.verticalGradient(listOf(primaryColor, Color.Transparent))
                    ),
                    SelectionHighlightPopUp()
                )
            )
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(color = primaryColor.copy(alpha = 0.1f)),
        backgroundColor = Color.Transparent
    )
}
