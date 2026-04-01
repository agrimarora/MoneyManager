package com.example.moneymanager.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanager.R
import com.example.moneymanager.common.model.ExpenseModel
import com.example.moneymanager.presentation.Viewmodel.AppViewModel
import com.example.moneymanager.presentation.Viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import java.text.SimpleDateFormat
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.moneymanager.ui.theme.transparentTextFieldColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AppViewModel = hiltViewModel(),
    chatViewModel: ChatViewModel = hiltViewModel(),
    firebaseAuth: FirebaseAuth,
    navcontroller: NavController,
    onOpenDrawer: () -> Unit
) {
    val state by viewModel.dashboardScreenstate
    val expenseState by viewModel.expenditureListScreenstate
    val goalState by viewModel.GoalScreenState
    var showChatDialog by remember { mutableStateOf(false) }
    val user = firebaseAuth.currentUser

    LaunchedEffect(true) {
        if (user != null) {
            viewModel.getuserbyUID(user.uid)
            viewModel.getGoals()
            viewModel.getAllExpenditure()
        }
    }

    val name = state.userdata?.Userdata?.name.orEmpty()
    val income = state.userdata?.Userdata?.income?.toDoubleOrNull() ?: 0.0
    val expenses = (expenseState.expenses ?: emptyList()).sortedByDescending { it.date }
    val totalExpenses = expenses.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
    val balance = income - totalExpenses
    
    val goals = goalState.goalList ?: emptyList()
    val totalGoalSavings = goals.sumOf { it.amount }
    val totalSavings = balance + totalGoalSavings

    // Calculate Spending breakdown
    val breakdownByType by remember(expenses) {
        derivedStateOf {
            val needsTotal = expenses.filter { it.category == "Need" }.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
            val wantsTotal = expenses.filter { it.category == "Want" }.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
            val goalsTotal = expenses.filter { it.category == "Goal" }.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
            val total = needsTotal + wantsTotal + goalsTotal
            
            if (total > 0) {
                Triple(
                    (needsTotal / total).toFloat(),
                    (wantsTotal / total).toFloat(),
                    (goalsTotal / total).toFloat()
                )
            } else {
                Triple(0.4f, 0.4f, 0.2f)
            }
        }
    }
    
    val needsPercent = breakdownByType.first
    val wantsPercent = breakdownByType.second
    val goalsPercent = breakdownByType.third

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(40.dp).clickable { navcontroller.navigate(com.example.moneymanager.Navigation.Routes.Goal) },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = "Goals",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Money Manager",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = (-0.5).sp
                )
            }
            
            Surface(
                modifier = Modifier.size(40.dp).clickable { onOpenDrawer() },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Hero Balance Section
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(
                                text = "TOTAL CAPITAL",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.8f),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "₹${"%,.2f".format(totalSavings)}",
                                style = MaterialTheme.typography.headlineLarge,
                                color = Color.White,
                                fontSize = 36.sp
                            )
                        }
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            MetricBadge("LIQUID", "₹${"%,.0f".format(balance.coerceAtLeast(0.0))}")
                            MetricBadge("GOALS", "₹${"%,.0f".format(totalGoalSavings)}")
                        }
                    }
                }
            }

            // AI Investment Insights
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().clickable { navcontroller.navigate(com.example.moneymanager.Navigation.Routes.News) },
                    shape = RoundedCornerShape(32.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("AI Investment Insights", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Your overall savings of ₹${"%,.0f".format(totalSavings.coerceAtLeast(0.0))} could grow to ₹${"%,.0f".format(totalSavings.coerceAtLeast(0.0) * 1.1)} by next year if invested in a balanced mutual fund. Tap to see more tips.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Quick Metrics Row
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        label = "Income",
                        value = "₹${"%,.0f".format(income)}",
                        icon = Icons.Default.TrendingUp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        label = "Total Savings",
                        value = "₹${"%,.0f".format(totalSavings.coerceAtLeast(0.0))}",
                        icon = Icons.Default.Savings,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // Bento Grid: Spending Logic & Recent Activity
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    // Spending Logic (Simplified Doughnut)
                    Surface(
                        modifier = Modifier.weight(0.45f).height(280.dp),
                        shape = RoundedCornerShape(32.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Spending Breakdown", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Icon(Icons.Default.MoreHoriz, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                             Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {
                                SpendingDoughnut(
                                    needs = needsPercent,
                                    wants = wantsPercent,
                                    goals = goalsPercent,
                                    primaryColor = MaterialTheme.colorScheme.primary,
                                    secondaryColor = MaterialTheme.colorScheme.secondary,
                                    tertiaryColor = MaterialTheme.colorScheme.tertiary
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Spent", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("₹${"%,.1f".format(totalExpenses/1000)}k", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            SpendingLegend("Needs", "${(needsPercent * 100).toInt()}%", MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            SpendingLegend("Wants", "${(wantsPercent * 100).toInt()}%", MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(8.dp))
                            SpendingLegend("Goals", "${(goalsPercent * 100).toInt()}%", MaterialTheme.colorScheme.tertiary)
                        }
                    }
                }
            }
            
            // Recent Activity
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Recent Expenses", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                "View All", 
                                color = MaterialTheme.colorScheme.primary, 
                                style = MaterialTheme.typography.labelMedium, 
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { navcontroller.navigate(com.example.moneymanager.Navigation.Routes.AddExpense) }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        if (expenses.isEmpty()) {
                            Text("No recent transactions", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            expenses.take(5).forEach { expense ->
                                TransactionItem(expense)
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // AI FAB
    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            onClick = { showChatDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 100.dp, end = 20.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(20.dp)
        ) {
            Icon(Icons.Default.Chat, contentDescription = "AI Assistant", tint = Color(0xFF084C00))
        }
    }

    if (showChatDialog) {
        ChatBotDialog(onDismiss = { showChatDialog = false }, viewModel = chatViewModel)
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
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "AI Intelligence",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    reverseLayout = true,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages.reversed()) { msg ->
                        val isUser = msg.role == "user"
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                        ) {
                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 20.dp,
                                    topEnd = 20.dp,
                                    bottomStart = if (isUser) 20.dp else 4.dp,
                                    bottomEnd = if (isUser) 4.dp else 20.dp
                                ),
                                color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHighest,
                                modifier = Modifier.widthIn(max = 280.dp)
                            ) {
                                Text(
                                    text = msg.content,
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isUser) Color(0xFF084C00) else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Query intelligence matrix...") },
                        shape = RoundedCornerShape(16.dp),
                        colors = transparentTextFieldColors()
                    )

                    Surface(
                        modifier = Modifier
                            .size(52.dp)
                            .clickable {
                                if (input.isNotBlank()) {
                                    viewModel.sendMessage(input)
                                    input = ""
                                }
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color(0xFF084C00))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricBadge(label: String, value: String) {
    Surface(
        color = Color.White.copy(alpha = 0.2f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f), fontSize = 8.sp)
            Text(text = value, style = MaterialTheme.typography.bodyMedium, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MetricCard(modifier: Modifier = Modifier, label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(14.dp),
                color = color.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
fun SpendingDoughnut(
    needs: Float,
    wants: Float,
    goals: Float,
    primaryColor: Color,
    secondaryColor: Color,
    tertiaryColor: Color
) {
    Canvas(modifier = Modifier.size(120.dp)) {
        val strokeWidth = 30f
        // Background
        drawArc(
            color = Color.LightGray.copy(alpha = 0.2f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )
        // Needs
        drawArc(
            color = primaryColor,
            startAngle = -90f,
            sweepAngle = 360f * needs,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )
        // Wants
        drawArc(
            color = secondaryColor,
            startAngle = -90f + (360f * needs),
            sweepAngle = 360f * wants,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )
        // Goals
        drawArc(
            color = tertiaryColor,
            startAngle = -90f + (360f * (needs + wants)),
            sweepAngle = 360f * goals,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun SpendingLegend(label: String, percent: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        }
        Text(percent, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TransactionItem(expense: ExpenseModel) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    when (expense.category) {
                        "Need" -> Icons.Default.ShoppingBag
                        "Goal" -> Icons.Default.Flag
                        else -> Icons.Default.Restaurant
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            val formattedDate = remember(expense.date) {
                val expenseDate = Date(expense.date)
                val now = Calendar.getInstance()
                val target = Calendar.getInstance().apply { time = expenseDate }
                
                when {
                    now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                    now.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR) -> {
                        "Today, " + SimpleDateFormat("hh:mm a", Locale.getDefault()).format(expenseDate)
                    }
                    now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                    now.get(Calendar.DAY_OF_YEAR) - target.get(Calendar.DAY_OF_YEAR) == 1 -> {
                        "Yesterday, " + SimpleDateFormat("hh:mm a", Locale.getDefault()).format(expenseDate)
                    }
                    else -> {
                        SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(expenseDate)
                    }
                }
            }
            Text(text = expense.description ?: "Unknown", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(text = "${expense.category} • $formattedDate", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = "-₹${expense.amount}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.ExtraBold)
            Text(text = "SETTLED", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
    }
}
