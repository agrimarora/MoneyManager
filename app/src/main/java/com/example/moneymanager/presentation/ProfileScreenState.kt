package com.example.moneymanager.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Colors
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanager.R
import com.example.moneymanager.presentation.Viewmodel.AppViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import com.example.moneymanager.Navigation.Routes
import com.example.moneymanager.common.model.UserData
import com.example.moneymanager.common.model.UserDataParent
import javax.annotation.meta.When

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
        name = state.userdata?.Userdata?.name.orEmpty()
        email = state.userdata?.Userdata?.email.orEmpty()
        phonenumber = state.userdata?.Userdata?.phoneNumber.orEmpty()
        income = state.userdata?.Userdata?.income.orEmpty()
        password = state.userdata?.Userdata?.password.orEmpty()
    }
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


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(100.dp))
                Text(
                    "User Details",
                    color = colorResource(id = R.color.prussian_Blue),
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                androidx.compose.material3.OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = {
                        Text("name")
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorResource(id = R.color.prussian_Blue),
                        unfocusedBorderColor = colorResource(id = R.color.prussian_Blue),
                    ),
                    readOnly = readOnly

                )
                Spacer(modifier = Modifier.height(16.dp))
                androidx.compose.material3.OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = {
                        Text("email")
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorResource(id = R.color.prussian_Blue),
                        unfocusedBorderColor = colorResource(id = R.color.prussian_Blue),
                    ),
                    readOnly = readOnly

                )
                Spacer(modifier = Modifier.height(16.dp))
                androidx.compose.material3.OutlinedTextField(
                    value = phonenumber,
                    onValueChange = { phonenumber = it },
                    label = {
                        Text("Phone Number")
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorResource(id = R.color.prussian_Blue),
                        unfocusedBorderColor = colorResource(id = R.color.prussian_Blue),
                    ),
                    readOnly = readOnly

                )
                Spacer(modifier = Modifier.height(16.dp))
                androidx.compose.material3.OutlinedTextField(
                    value = income,
                    onValueChange = { income = it },
                    label = {
                        Text("Income")
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorResource(id = R.color.prussian_Blue),
                        unfocusedBorderColor = colorResource(id = R.color.prussian_Blue),
                    ),
                    readOnly = readOnly

                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
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
                            viewModel.getuserbyUID(firebaseAuth.currentUser?.uid ?: "")


                        }
                    },
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.prussian_Blue))
                ) {
                    Text(
                        text = if (readOnly) "Edit" else "Save",
                        color = Color.White
                    )

                }
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        firebaseAuth.signOut()
                        navController.navigate(Routes.Login)
                    }, colors = ButtonDefaults.buttonColors(colorResource(R.color.white)),
                    border = androidx.compose.foundation.BorderStroke(
                        2.dp,
                        colorResource(R.color.prussian_Blue)
                    )
                ) {
                    Text(text = "Log Out", color = colorResource(R.color.prussian_Blue))

                }


            }
        }
    }
}