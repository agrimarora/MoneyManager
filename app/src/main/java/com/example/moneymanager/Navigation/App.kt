package com.example.moneymanager.Navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.moneymanager.presentation.AddExpenseScreen
import com.example.moneymanager.presentation.GoalScreen
import com.example.moneymanager.presentation.HomeScreen
import com.example.moneymanager.presentation.ProfileScreen
import com.example.moneymanager.presentation.ReportScreen
import com.example.moneymanager.presentation.SignUpScrenn

import com.example.moneymanager.presentation.logInScreen
import com.google.firebase.auth.FirebaseAuth


@Composable
fun App(navController: NavHostController, firebaseAuth: FirebaseAuth) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val bottomNavItems = listOf(
        Pair(Routes.Dashboard, "Home"),
        Pair(Routes.AddExpense, "Expenses"),
        Pair(Routes.Report, "Report"),
        Pair(Routes.Profile, "Profile")
    )
    val showBottomNav = currentRoute in bottomNavItems.map { it.first::class.qualifiedName!! }
    Scaffold(
bottomBar = {
if (showBottomNav) {


    BottomNavigation(modifier = Modifier.height(86.dp).fillMaxWidth().padding(5.dp), backgroundColor = Color.White) {
        bottomNavItems.forEach { (screen, label) ->
            val route = screen::class.qualifiedName!!
            BottomNavigationItem(
                selected = currentRoute == route,
                onClick = {
                    navController.navigate(screen) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    when (label) {
                        "Home" -> Icon(Icons.Default.Home, contentDescription = null)
                        "Expenses" -> Icon(Icons.Default.StackedBarChart, contentDescription = null)
                        "Report" -> Icon(Icons.Default.Receipt, contentDescription = null)
                        "Profile" -> Icon(Icons.Default.Person, contentDescription = null)
                    }
                },
                label = { Text(label) }
            )
        }
    }

}
}
    ) { paddingvalues ->
        Box(modifier = Modifier.padding(paddingvalues)) {

            val startDestination = if (firebaseAuth.currentUser != null) {
                Routes.Dashboard
            } else {
                Routes.Login
            }
            NavHost(navController = navController, startDestination = startDestination)
            {
                composable<Routes.Login> {
                    logInScreen(navController = navController)
                }
                composable<Routes.Goal> {
                    GoalScreen(navController=navController)
                }

                composable<Routes.SignUp> {
                    SignUpScrenn(navController = navController)


                }
                composable<Routes.Dashboard> {
                    HomeScreen(firebaseAuth = firebaseAuth, navcontroller = navController)

                }
                composable<Routes.AddExpense> {
                    AddExpenseScreen(navController = navController, firebaseAuth = firebaseAuth)

                }
                composable<Routes.Report> {
                    ReportScreen(firebaseAuth = firebaseAuth)

                }
                composable<Routes.Profile> {
                    ProfileScreen(
                        navController = navController,firebaseAuth = firebaseAuth)

                }

            }
        }
    }
}

