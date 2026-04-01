package com.example.moneymanager.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moneymanager.Navigation.Routes
import com.example.moneymanager.R
import com.example.moneymanager.common.model.UserData
import com.example.moneymanager.presentation.Viewmodel.AppViewModel


@Composable

fun logInScreen(viewModel: AppViewModel = hiltViewModel(),navController: NavController) {
    val context = LocalContext.current
    var state=viewModel.logINScreenstate
    var userEmail by rememberSaveable { mutableStateOf("") }
    var userPassword by rememberSaveable { mutableStateOf("") }
    var currentUserState by rememberSaveable { mutableStateOf("") }
    var showtext by rememberSaveable { mutableStateOf(false) }
    when {
        state.value.isLoading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }


        else -> {
            if (state.value.success == true) {
                navController.navigate(Routes.Dashboard)

            } else {


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally,


                    ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text("Log In here ", color = colorResource(id = R.color.prussian_Blue))

                    OutlinedTextField(
                        value = userEmail,
                        onValueChange = { userEmail = it },
                        label = {
                            Text("Email")
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(id = R.color.prussian_Blue),
                            unfocusedBorderColor = colorResource(id = R.color.prussian_Blue),
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = userPassword,
                        onValueChange = { userPassword = it },
                        label = { Text("Password") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(id = R.color.prussian_Blue),
                            unfocusedBorderColor = colorResource(id = R.color.prussian_Blue),
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    if (showtext) {
                        Text(text = "Enter valid email and password", color = Color.Red)
                    }
                    if (!state.value.error.isNullOrEmpty()) {
                        Text(text = "Something Went Wrong!\n${state.value.error}", color = Color.Red)
                    }

                    Button(onClick = {
                        if (userEmail!="" && userPassword !="") {
                            viewModel.LoginUser(
                                UserData(
                                    email = userEmail,
                                    password = userPassword
                                )
                            )


                        } else {
                            showtext = true

                        }
                    }, colors = ButtonDefaults.buttonColors(colorResource(R.color.prussian_Blue))) {
                        Text("Log IN")

                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Text("Dont have an account? Sign Up", modifier = Modifier.clickable{navController.navigate(
                        Routes.SignUp)} )

                }

            }
        }
}}