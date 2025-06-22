package com.example.moneymanager.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.navigation.NavController
import com.example.moneymanager.Navigation.Routes

import com.example.moneymanager.R
import com.example.moneymanager.common.model.UserData
import com.example.moneymanager.presentation.Viewmodel.AppViewModel


@Composable
fun SignUpScrenn(
    viewModel: AppViewModel = hiltViewModel(),
    navController: NavController
) {
    val state = viewModel.signupScreenstate
    var UserPassword by remember { mutableStateOf("") }
    var UserConformPassword by remember { mutableStateOf("") }
    var UserEmail by remember { mutableStateOf("") }
    var UserName by remember { mutableStateOf("") }
    var UserPhoneNumber by remember { mutableStateOf("") }
    var UserIncome by remember { mutableStateOf("") }
    var context=LocalContext.current

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
                navController.navigate(Routes.Login)

            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(50.dp))
                    Text(
                        text = "Sign Up here",
                        color = colorResource(id = R.color.prussian_Blue),
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(30.dp))

                    OutlinedTextField(
                        value = UserName,
                        onValueChange = { UserName = it },
                        label = { Text(text = "Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(id = R.color.prussian_Blue),
                            unfocusedBorderColor = colorResource(id = R.color.prussian_Blue),
                        )
                    )
                    Spacer(modifier = Modifier.height(15.dp))

                    OutlinedTextField(
                        value = UserEmail,
                        onValueChange = { UserEmail = it },
                        label = { Text(text = "Email") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(id = R.color.prussian_Blue),
                            unfocusedBorderColor = colorResource(id = R.color.prussian_Blue)
                        )
                    )
                    Spacer(modifier = Modifier.height(15.dp))

                    OutlinedTextField(
                        value = UserPhoneNumber,
                        onValueChange = { UserPhoneNumber = it },
                        label = { Text(text = "Phone Number") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(id = R.color.prussian_Blue),
                            unfocusedBorderColor = colorResource(id = R.color.prussian_Blue)
                        )
                    )
                    Spacer(modifier = Modifier.height(15.dp))

                    OutlinedTextField(
                        value = UserPassword,
                        onValueChange = { UserPassword = it },
                        label = { Text(text = "Password") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(id = R.color.prussian_Blue),
                            unfocusedBorderColor = colorResource(id = R.color.prussian_Blue)
                        )
                    )
                    Spacer(modifier = Modifier.height(15.dp))

                    OutlinedTextField(
                        value = UserConformPassword,
                        onValueChange = { UserConformPassword = it },
                        label = { Text(text = "Confirm Password") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(id = R.color.prussian_Blue),
                            unfocusedBorderColor = colorResource(id = R.color.prussian_Blue)
                        )
                    )
                    Spacer(modifier = Modifier.height(15.dp))

                    OutlinedTextField(
                        value = UserIncome,
                        onValueChange = { UserIncome= it },
                        label = { Text(text = "Income") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(id = R.color.prussian_Blue),
                            unfocusedBorderColor = colorResource(id = R.color.prussian_Blue)
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            viewModel.createUser(
                                UserData(
                                    name = UserName,
                                    email = UserEmail,
                                    password = UserPassword,
                                    phoneNumber = UserPhoneNumber
                                    ,income = UserIncome
                                )
                            )

//                        else{
//                            UserPassword=""
//                            UserConformPassword=""
//                            UserEmail=""
//                            UserName=""
//                            UserPhoneNumber=""
//                            Toast.makeText(context, "Something went wrong! Try Again", Toast.LENGTH_SHORT).show()
//
//                        }
                        },
                        modifier = Modifier
                            .height(50.dp)
                            .width(300.dp),
                        colors = ButtonDefaults.buttonColors(colorResource(R.color.prussian_Blue))
                    ) {
                        Text(text = "Sign Up", color = colorResource(id = R.color.white))
                    }
                }
            }
        }
    }
}
