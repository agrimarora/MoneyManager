package com.example.moneymanager.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanager.Navigation.Routes
import com.example.moneymanager.R
import com.example.moneymanager.common.model.UserData
import com.example.moneymanager.presentation.Viewmodel.AppViewModel
import com.example.moneymanager.ui.theme.transparentTextFieldColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScrenn(
    viewModel: AppViewModel = hiltViewModel(),
    navController: NavController
) {
    val state = viewModel.signupScreenstate
    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userPhone by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var userIncome by remember { mutableStateOf("") }
    var showLocalError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Subtle Background Texture
        Box(
            modifier = Modifier.fillMaxSize().alpha(0.15f)
        ) {
            Box(
                modifier = Modifier
                    .size(600.dp)
                    .align(Alignment.TopStart)
                    .blur(140.dp)
                    .background(Brush.radialGradient(colors = listOf(MaterialTheme.colorScheme.primary, Color.Transparent)))
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .systemBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Brand Identity
            Icon(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "Brand Logo",
                tint = Color.Unspecified,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Sign Up",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Create an Account",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Auth Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                shadowElevation = 10.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Fields
                    SignUpField("Name", "Enter your name", Icons.Default.Person, userName) { userName = it }
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    SignUpField("Email", "Enter your email", Icons.Default.AlternateEmail, userEmail) { userEmail = it }
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    SignUpField("Phone Number", "Enter your mobile number", Icons.Default.Phone, userPhone) { userPhone = it }
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    SignUpField("Income", "Enter your base income", Icons.Default.MonetizationOn, userIncome) { userIncome = it }
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Password",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = userPassword,
                        onValueChange = { userPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("••••••••••••", fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = transparentTextFieldColors(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (showLocalError) {
                        Text(text = "Please fill in all protocol fields", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium)
                    }
                    if (!state.value.error.isNullOrEmpty()) {
                        Text(text = state.value.error ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Button
                    Button(
                        onClick = {
                            if (userName.isNotBlank() && userEmail.isNotBlank() && userPassword.isNotBlank() && userIncome.isNotBlank()) {
                                showLocalError = false
                                viewModel.createUser(
                                    UserData(
                                        name = userName,
                                        email = userEmail,
                                        password = userPassword,
                                        phoneNumber = userPhone,
                                        income = userIncome
                                    )
                                )
                            } else {
                                showLocalError = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.primaryContainer
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (state.value.isLoading) {
                                CircularProgressIndicator(color = Color(0xFF084C00), modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    "SIGN UP",
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp,
                                    color = Color(0xFF084C00)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            TextButton(onClick = { navController.popBackStack() }) {
                Text(
                    text = "Already have an account? Login",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Success Navigation
        LaunchedEffect(state.value.success) {
            if (state.value.success == true) {
                navController.navigate(Routes.Dashboard) {
                    popUpTo(Routes.SignUp) { inclusive = true }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpField(
    label: String,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, fontSize = 14.sp) },
            leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp)) },
            colors = transparentTextFieldColors(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )
    }
}
