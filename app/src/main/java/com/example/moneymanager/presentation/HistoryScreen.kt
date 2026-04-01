package com.example.moneymanager.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanager.presentation.Viewmodel.AppViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController, viewModel: AppViewModel = hiltViewModel()) {
    val expenseState by viewModel.expenditureListScreenstate
    val userState by viewModel.dashboardScreenstate
    
    val previousMonthStats = remember(expenseState.expenses) {
        val expenses = expenseState.expenses ?: emptyList()
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        val prevMonth = calendar.get(Calendar.MONTH)
        val prevYear = calendar.get(Calendar.YEAR)
        
        val prevMonthExpenses = expenses.filter {
            val d = Calendar.getInstance().apply { time = Date(it.date) }
            d.get(Calendar.MONTH) == prevMonth && d.get(Calendar.YEAR) == prevYear
        }
        
        val totalSpent = prevMonthExpenses.filter { it.category != "Goal" }.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
        val income = userState.userdata?.Userdata?.income?.toDoubleOrNull() ?: 0.0
        val savings = (income - totalSpent).coerceAtLeast(0.0)
        
        Triple(totalSpent, savings, prevMonthExpenses)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Spending History", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
        ) {
            item {
                HistoryHeroCard(totalSpent = previousMonthStats.first, savings = previousMonthStats.second)
            }
            
            item {
                Text("Previous Month Transactions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            
            if (previousMonthStats.third.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        Text("No transactions found for the previous month.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(previousMonthStats.third.size) { index ->
                    val expense = previousMonthStats.third[index]
                    TransactionItem(expense)
                }
            }
        }
    }
}

@Composable
fun HistoryHeroCard(totalSpent: Double, savings: Double) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(180.dp),
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(modifier = Modifier.padding(24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("PREVIOUS MONTH", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text("₹${"%,.0f".format(totalSpent)}", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
                Text("Total Expenditure", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    modifier = Modifier.size(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Savings, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(32.dp))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("₹${"%,.0f".format(savings)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                Text("Savings", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
