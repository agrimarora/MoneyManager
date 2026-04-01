package com.example.moneymanager.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanager.R
import com.example.moneymanager.presentation.Viewmodel.AppViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.moneymanager.Navigation.Routes
import com.example.moneymanager.common.model.UserData
import com.example.moneymanager.common.model.UserDataParent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: AppViewModel = hiltViewModel(),
    navController: NavController,
    firebaseAuth: FirebaseAuth,
) {
    val state = viewModel.ProfileScreenState.value
    val user = firebaseAuth.currentUser
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phonenumber by remember { mutableStateOf("") }
    var income by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var readOnly by remember { mutableStateOf(true) }

    LaunchedEffect(true) {
        if (user != null) {
            viewModel.getuserbyUID(user.uid)
        }
    }

    LaunchedEffect(state.userdata) {
        state.userdata?.Userdata?.let {
            name = it.name.orEmpty()
            email = it.email.orEmpty()
            phonenumber = it.phoneNumber.orEmpty()
            income = it.income.orEmpty()
            password = it.password.orEmpty()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.ExtraBold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (!state.error.isNullOrEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${state.error}")
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    // Profile Avatar Placeholder
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = name.take(1).uppercase(),
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(text = email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(48.dp))

                    // Account Details Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("User Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(
                                    text = if (readOnly) "Modify" else "Save",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable {
                                        if (readOnly) {
                                            readOnly = false
                                        } else {
                                            viewModel.updateuser(
                                                userDataParent = UserDataParent(
                                                    nodeID = firebaseAuth.currentUser?.uid,
                                                    Userdata = UserData(
                                                        name = name,
                                                        email = email,
                                                        phoneNumber = phonenumber,
                                                        income = income,
                                                        password = password
                                                    )
                                                )
                                            )
                                            readOnly = true
                                        }
                                    }
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))

                            ProfileField("FULL NAME", name, readOnly) { name = it }
                            Spacer(modifier = Modifier.height(16.dp))
                            ProfileField("EMAIL", email, readOnly) { email = it }
                            Spacer(modifier = Modifier.height(16.dp))
                            ProfileField("PHONE NUMBER", phonenumber, readOnly) { phonenumber = it }
                            Spacer(modifier = Modifier.height(16.dp))
                            ProfileField("INCOME", income, readOnly) { income = it }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Security & Session
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable {
                                    firebaseAuth.signOut()
                                    navController.navigate(Routes.Login) {
                                        popUpTo(0)
                                    }
                                },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text("Logout", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileField(label: String, value: String, readOnly: Boolean, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (readOnly) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
        } else {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
        }
    }
}