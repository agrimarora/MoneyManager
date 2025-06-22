package com.example.moneymanager.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneymanager.presentation.Viewmodel.AppViewModel
import com.example.moneymanager.presentation.Viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ReportScreen(
    viewModel: AppViewModel = hiltViewModel(),
    chatviewModel: ChatViewModel = hiltViewModel(),
    firebaseAuth: FirebaseAuth
) {
    val user = firebaseAuth.currentUser

    LaunchedEffect(true) {
        if (user != null) {
            viewModel.getuserbyUID(user.uid)
        }
    }

    val state = viewModel.dashboardScreenstate.value
    val userdata = state.userdata?.Userdata
    val expenseState = viewModel.expenditureListScreenstate.value

    LaunchedEffect(userdata) {
        if (userdata != null) {
            chatviewModel.sendReportMessage(userdata, expenseState.expenses ?: emptyList())
        }
    }

    val reportMessages = chatviewModel.reportMessages

    Column(
        modifier = Modifier
            .fillMaxSize()

            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "AI Generated Financial Report",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Divider(color = Color.LightGray)

        if (reportMessages.isEmpty() || reportMessages.none { it.role == "assistant" }) {
            // Show loading message
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reportMessages) { message ->
                    if (message.role == "assistant") {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFEEF7FF)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = message.content,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
