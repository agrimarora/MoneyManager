package com.example.moneymanager.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneymanager.R
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanager.Navigation.Routes
import com.example.moneymanager.presentation.Viewmodel.AppViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.example.moneymanager.common.model.ExpenseModel
import com.google.firebase.auth.FirebaseAuth
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun AddExpenseScreen (viewmodel: AppViewModel= hiltViewModel(),navController: NavController,firebaseAuth: FirebaseAuth) {
    val state = viewmodel.addExpenseScreenstate

    var showAddDialog by remember { mutableStateOf(false) }



    val expenseState = viewmodel.expenditureListScreenstate.value
    var expenses = expenseState.expenses ?: emptyList()
    expenses = (expenseState.expenses ?: emptyList())
        .sortedBy { it.date }

    when {
        state.value.isLoading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = colorResource(id = R.color.prussian_Blue))
            }
        }

        !state.value.error.isNullOrEmpty() -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Something Went Wrong!\n${state.value.error}")
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        else -> {

            if (state.value.success == true) {
                navController.navigate(Routes.Dashboard)


            } else {
                val steps = 5
                val screenWidth = LocalConfiguration.current.screenWidthDp.dp

                val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
                val dateLabels: List<String> = expenses.map { expense ->
                    dateFormat.format(Date(expense.date)) // assuming expense.date is in millis
                }

                val pointsData: List<Point> = expenses.mapIndexed { index, expense ->
                    Point(index.toFloat(), expense.amount.toFloat())
                }
                val xAxisData = AxisData.Builder()
                    .axisStepSize(screenWidth / (pointsData.size.coerceAtLeast(1)))
//                .backgroundColor(Color.Blue)
                    .steps(pointsData.size - 1)
                    .labelData { i -> dateLabels.getOrNull(i) ?: "" }
                    .labelAndAxisLinePadding(15.dp)
                    .axisLineColor(colorResource(R.color.prussian_Blue))
                    .build()

                val yAxisData = AxisData.Builder()
                    .steps(steps)

                    .labelAndAxisLinePadding(20.dp)
                    .labelData { i ->
                        val yScale = 100 / steps
                        (i * yScale).toString()
                    }.axisLineColor(colorResource(R.color.prussian_Blue)).build()
                val lineChartData = LineChartData(
                    linePlotData = LinePlotData(
                        lines = listOf(
                            Line(
                                dataPoints = pointsData,
                                LineStyle(color = colorResource(R.color.prussian_Blue)),
                                IntersectionPoint(color = colorResource(R.color.prussian_Blue)),
                                SelectionHighlightPoint(color = colorResource(R.color.prussian_Blue)),
                                ShadowUnderLine(
                                    alpha = 0.4f,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            colorResource(R.color.prussian_Blue),
                                            Color.Transparent
                                        )
                                    )
                                ),
                                SelectionHighlightPopUp()
                            )
                        ),
                    ),
                    xAxisData = xAxisData,
                    yAxisData = yAxisData,
                    gridLines = GridLines(),
                    backgroundColor = Color.White
                )
                Box(modifier = Modifier.fillMaxSize()) {

                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text("Statistic Expenditure")
                        if (pointsData.isNullOrEmpty()) {
                            Text("No Expenses Yet", fontSize = 24.sp, color = Color.Black)
                        } else {
                            LineChart(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(260.dp),
                                lineChartData = lineChartData
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Expenses",
                            fontSize = 22.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            items(expenses) { expense ->
                                ExpenseItem(expense = expense)
                            }
                        }}
                        FloatingActionButton(
                            onClick = { showAddDialog = true },
                            modifier = Modifier
                                .align (Alignment.BottomEnd)
                                .padding(16.dp),
                            containerColor = colorResource(id = R.color.prussian_Blue)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Expense",
                                tint = Color.White
                            )
                        }
                    if (showAddDialog) {
                        AddExpenseDialog(
                            onDismiss = {
                                showAddDialog = false
                            }, firebaseAuth = firebaseAuth

                        )
                    }






                    }
                }
            }
        }
    }
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(
    viewmodel: AppViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    firebaseAuth: FirebaseAuth
) {
    var discription by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Need", "Want")
    var selectedOption by remember { mutableStateOf(options[0]) }
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val user = firebaseAuth.currentUser

    val calendarState = rememberSheetState()
    val selectedDateMillis = remember { mutableStateOf<Long?>(null) }
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Add Expense",
                    color = colorResource(id = R.color.prussian_Blue),
                    fontSize = 25.sp
                )

                Spacer(modifier = Modifier.height(30.dp))

                OutlinedTextField(
                    value = discription,
                    onValueChange = { discription = it },
                    label = { Text("Description") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorResource(id = R.color.prussian_Blue),
                        unfocusedBorderColor = colorResource(id = R.color.prussian_Blue),
                    )
                )

                Spacer(modifier = Modifier.height(15.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount in Rupees") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorResource(id = R.color.prussian_Blue),
                        unfocusedBorderColor = colorResource(id = R.color.prussian_Blue),
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )

                Spacer(modifier = Modifier.height(15.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                ) {
                    OutlinedTextField(
                        value = selectedOption,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier.menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(id = R.color.prussian_Blue),
                            unfocusedBorderColor = colorResource(id = R.color.prussian_Blue),
                        ),
                        leadingIcon = {
                            val color = if (selectedOption == "Need") Color.Green else Color.Red
                            Spacer(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(color, CircleShape)
                            )
                        }
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        val color =
                                            if (option == "Need") Color.Green else Color.Red
                                        Spacer(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .background(color, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(option)
                                    }
                                },
                                onClick = {
                                    selectedOption = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                // 🔘 Select Date Button
                Button(
                    onClick = { calendarState.show() },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.prussian_Blue))
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = selectedDateMillis.value?.let {
                            dateFormatter.format(Date(it))
                        } ?: "Select Date",
                        color = Color.White
                    )
                }

                // 🗓️ Calendar Dialog
                CalendarDialog(
                    state = calendarState,
                    config = CalendarConfig(
                        yearSelection = true,
                        monthSelection = true,
                        style = CalendarStyle.MONTH,
                        disabledDates = generateDisabledFutureDates()
                    ),
                    selection = CalendarSelection.Date { localDate ->
                        selectedDateMillis.value = localDate
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant().toEpochMilli()
                    }
                )

                Spacer(modifier = Modifier.height(30.dp))

                Button(onClick = {
                    if (user != null && selectedDateMillis.value != null) {
                        viewmodel.AddExpense(
                            ExpenseModel(
                                description = discription,
                                category = selectedOption,
                                amount = amount,
                                date = selectedDateMillis.value!!
                            )
                        )
                        onDismiss()
                    }
                },
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.prussian_Blue))) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Add", color = Color.White)
                }
            }
        }
    }
}
fun generateDisabledFutureDates(): List<LocalDate> {
    val today = LocalDate.now()
    val futureDates = mutableListOf<LocalDate>()
    for (i in 1..800) {
        futureDates.add(today.plusDays(i.toLong()))
    }
    return futureDates
}








