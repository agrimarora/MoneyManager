package com.example.moneymanager.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(navController: NavController) {
    val newsList = listOf(
        NewsItem("Market Update", "Nifty 50 touches new all-time high amidst positive global cues.", "2 hours ago", Icons.Default.TrendingUp, Color(0xFF4CAF50)),
        NewsItem("Investment Tip", "Consider diversifying into Index Funds for long-term wealth creation.", "5 hours ago", Icons.Default.Lightbulb, Color(0xFFFFC107)),
        NewsItem("Global Markets", "US inflation data lower than expected, tech stocks rally.", "Yesterday", Icons.Default.Public, Color(0xFF2196F3)),
        NewsItem("Financial Planning", "How to save your first 1 Lakh: A step-by-step guide.", "2 days ago", Icons.Default.AccountBalance, Color(0xFF9C27B0))
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Financial News", fontWeight = FontWeight.ExtraBold) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
        ) {
            item {
                AIInsightsSection()
            }
            items(newsList) { news ->
                NewsCard(news)
            }
        }
    }
}

@Composable
fun AIInsightsSection() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI Investment Insights", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Based on your recent 'Wants' spending, allocating an extra ₹2,000 to your 'Emergency Fund' goal could reach your target 3 months early.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun NewsCard(news: NewsItem) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = news.color.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(news.icon, contentDescription = null, tint = news.color)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(news.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(news.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Text(news.time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

data class NewsItem(val title: String, val description: String, val time: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val color: Color)
