package com.example.moneymanager.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
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
import com.example.moneymanager.Navigation.Routes
import com.example.moneymanager.R
import com.example.moneymanager.common.model.ExpenseModel
import com.example.moneymanager.common.model.UserData
import com.example.moneymanager.presentation.Viewmodel.AppViewModel
import com.example.moneymanager.presentation.Viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: AppViewModel = hiltViewModel(),
    chatViewModel: ChatViewModel = hiltViewModel(),
    firebaseAuth: FirebaseAuth,
    navcontroller: NavController
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val user = firebaseAuth.currentUser
    val state = viewModel.dashboardScreenstate.value
    val expenseState = viewModel.expenditureListScreenstate.value
    var name by remember { mutableStateOf("") }
    var showChatDialog by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        if (user != null) {
            viewModel.getuserbyUID(user.uid)
        }
    }

    name = state.userdata?.Userdata?.name.orEmpty()
    var expenses = (expenseState.expenses ?: emptyList()).sortedBy { it.date }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onGoalsClick = {
                    coroutineScope.launch { drawerState.close() }
                },
                onSavingsClick = {
                    coroutineScope.launch { drawerState.close() }
                },
                navcontroller = navcontroller
            )
        },
        gesturesEnabled = true,
        scrimColor = Color.Black.copy(alpha = 0.5f),
    ) {
        // ✅ All main screen content goes *inside* this lambda
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
                        Text("Something Went Wrong!\n${state.error}")
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {

                        // 🟦 Header Row with Menu + Greeting
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, top = 22.dp, end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                coroutineScope.launch { drawerState.open() }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = null,
                                    tint = colorResource(id = R.color.prussian_Blue)
                                )
                            }
                            Column {
                                Text("Hello,", fontSize = 24.sp, color = Color.Black)
                                Text(name, fontSize = 29.sp, color = Color.Black)
                            }
                        }

                        // 🟩 Total Balance Card
                        CardItem(
                            viewModel = viewModel,
                            modifier = Modifier.padding(top = 16.dp)
                        )

                        // 🟨 Expenses Title
                        Text(
                            text = "Expenses",
                            fontSize = 22.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )

                        // 🟧 Expense List
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            items(expenses.reversed()) { expense ->
                                ExpenseItem(expense = expense)
                            }
                        }
                    }

                    FloatingActionButton(
                        onClick = { showChatDialog = true },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        containerColor = colorResource(id = R.color.prussian_Blue)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "Chatbot",
                            tint = Color.White
                        )
                    }

                    // 🗨️ Chatbot Dialog
                    if (showChatDialog) {
                        ChatBotDialog(
                            onDismiss = { showChatDialog = false },
                            viewModel = chatViewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBotDialog(
    onDismiss: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val messages = viewModel.messages
    var input by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("AI Chat", fontSize = 22.sp, color = Color.Black)

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    reverseLayout = true
                ) {
                    items(messages.reversed()) { msg ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            contentAlignment = if (msg.role == "user") Alignment.CenterEnd else Alignment.CenterStart
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (msg.role == "user") {
                                    colorResource(R.color.prussian_Blue)
                                } else {
                                    colorResource(R.color.Mud_Grey)
                                },
                                modifier = Modifier
                                    .defaultMinSize(minHeight = 40.dp)
                                    .padding(4.dp)
                            )
                            {
                                Text(
                                    text = msg.content,
                                    modifier = Modifier.padding(8.dp),
                                    fontSize = 16.sp, color = if (msg.role == "user") {
                                        colorResource(R.color.white)
                                    } else {
                                        colorResource(R.color.prussian_Blue)
                                    }
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type a message...") }
                    )

                    IconButton(onClick = {
                        viewModel.sendMessage(input)
                        input = ""
                    }) {
                        Icon(Icons.Default.Send, contentDescription = "Send")
                    }
                }
            }
        }
    }
}


@Composable
fun CardItem(
    modifier: Modifier = Modifier,
    viewModel: AppViewModel = hiltViewModel(),

    ) {
    val state = viewModel.dashboardScreenstate.value
    val expenseState = viewModel.expenditureListScreenstate.value
    var showDialog by remember { mutableStateOf(false) }
    var balance by remember { mutableStateOf("----") }
    var alertbox by remember { mutableStateOf(balance) }

    LaunchedEffect(state.userdata?.Userdata?.income) {
        state.userdata?.Userdata?.income?.let {
            balance = it
            alertbox = it
        }
    }

    val totalExpensesValue = expenseState.expenses?.sumOf {
        it.amount.toDoubleOrNull() ?: 0.0
    } ?: 0.0

    val incomeValue = balance.toDoubleOrNull() ?: 0.0
    val savings = incomeValue - totalExpensesValue

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Enter Total Balance") },
            text = {
                OutlinedTextField(
                    value = alertbox,
                    onValueChange = { alertbox = it },
                    label = { Text("Total Balance") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.AddIncome(UserData(income = alertbox))
                    balance = alertbox
                    showDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                alertbox = balance
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(R.color.prussian_Blue))
            .padding(16.dp)
    ) {
        Row {
            Column(
                modifier = Modifier.clickable { showDialog = true }
            ) {
                Text("Total Balance", fontSize = 20.sp, color = Color.White)
                Spacer(modifier = Modifier.height(10.dp))
                Text("Rs.$balance", fontSize = 24.sp, color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(28.dp))
        Row {
            Column {
                Text("Savings", fontSize = 20.sp, color = Color.White)
                Spacer(modifier = Modifier.height(10.dp))
                Text("Rs.${"%.2f".format(savings)}", fontSize = 24.sp, color = Color.White)
            }
            Spacer(modifier = Modifier.width(130.dp))
            Column {
                Text("Expenditure", fontSize = 20.sp, color = Color.White)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "Rs.${"%.2f".format(totalExpensesValue)}",
                    fontSize = 24.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ExpenseList(
    expenses: List<Any?>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Expenses",
            fontSize = 22.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(expenses) { expense ->
                ExpenseItem(expense = expense as ExpenseModel)
            }
        }
    }
}

@Composable
fun ExpenseItem(expense: ExpenseModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF0F0F0))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(
                    if (expense.category.equals("Need", ignoreCase = true)) Color(0xFF4CAF50)
                    else Color(0xFFF44336)
                )
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = expense.description ?: "Unnamed",
                fontSize = 18.sp,
                color = colorResource(R.color.prussian_Blue)
            )
            Text(text = expense.category ?: "Category", fontSize = 14.sp, color = Color.Gray)
        }

        Text(
            text = "Rs.${expense.amount}",
            fontSize = 18.sp,
            color = Color.Black
        )
    }
}
@Composable
fun DrawerContent(
    onGoalsClick: () -> Unit,
    onSavingsClick: () -> Unit,
    navcontroller: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(250.dp)
            .background(colorResource(R.color.white))
            .padding(16.dp)
    ) {
        Text("Menu", fontSize = 22.sp, color = colorResource(R.color.prussian_Blue))
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "🏆 Goals",
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navcontroller.navigate(Routes.Goal)}
                .padding(vertical = 8.dp)
        )

        Text(
            text = "💰 Savings",
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSavingsClick() }
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Divider()

        Text(
            text = "⚙ Settings",
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* Handle settings */ }
                .padding(vertical = 8.dp)
        )
    }
}
