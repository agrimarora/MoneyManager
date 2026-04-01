package com.example.moneymanager.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
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
fun logInScreen(viewModel: AppViewModel = hiltViewModel(), navController: NavController) {
    val state = viewModel.logINScreenstate
    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var showLocalError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Subtle Background Texture / Kinetic Lights
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.2f)
        ) {
            Box(
                modifier = Modifier
                    .size(500.dp)
                    .align(Alignment.TopEnd)
                    .blur(120.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(MaterialTheme.colorScheme.primary, Color.Transparent)
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .size(500.dp)
                    .align(Alignment.BottomStart)
                    .blur(120.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(MaterialTheme.colorScheme.secondary, Color.Transparent)
                        )
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Brand Identity
            Icon(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "Brand Logo",
                tint = Color.Unspecified,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "MONEYMANAGER",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Money Manager",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Auth Card (Glass-card)
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
                    // Email Field
                    Text(
                        text = "Email",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = userEmail,
                        onValueChange = { userEmail = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter your email", fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        colors = transparentTextFieldColors(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Password Field
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "Password",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Forgot Password?",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = userPassword,
                        onValueChange = { userPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("••••••••••••", fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        trailingIcon = { Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = transparentTextFieldColors(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (showLocalError) {
                        Text(text = "Enter valid email and password", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium)
                    }
                    if (!state.value.error.isNullOrEmpty()) {
                        Text(text = state.value.error ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Button (Primary Gradient)
                    Button(
                        onClick = {
                            if (userEmail.isNotBlank() && userPassword.isNotBlank()) {
                                showLocalError = false
                                viewModel.LoginUser(UserData(email = userEmail, password = userPassword))
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
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    "LOGIN",
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp,
                                    color = Color(0xFF084C00) // Dark green for contrast on neon
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Secondary Actions
            TextButton(onClick = { navController.navigate(Routes.SignUp) }) {
                Text(
                    text = "Don't have an account? Sign Up",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Success Navigation
        LaunchedEffect(state.value.success) {
            if (state.value.success == true) {
                navController.navigate(Routes.Dashboard) {
                    popUpTo(Routes.Login) { inclusive = true }
                }
            }
        }
    }
}