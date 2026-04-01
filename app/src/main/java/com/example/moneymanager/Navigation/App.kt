package com.example.moneymanager.Navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.moneymanager.presentation.AddExpenseScreen
import com.example.moneymanager.presentation.GoalScreen
import com.example.moneymanager.presentation.HistoryScreen
import com.example.moneymanager.presentation.HomeScreen
import com.example.moneymanager.presentation.NewsScreen
import com.example.moneymanager.presentation.ProfileScreen
import com.example.moneymanager.presentation.ReportScreen
import com.example.moneymanager.presentation.SignUpScrenn
import com.example.moneymanager.presentation.SplashScreen

import com.example.moneymanager.presentation.logInScreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


@Composable
fun App(navController: NavHostController, firebaseAuth: FirebaseAuth) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val bottomNavItems = listOf(
        Pair(Routes.Dashboard, "Home"),
        Pair(Routes.AddExpense, "Expenses"),
        Pair(Routes.Report, "Report"),
        Pair(Routes.Profile, "Profile")
    )
    val showBottomNav = currentRoute in bottomNavItems.map { it.first::class.qualifiedName!! }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerTonalElevation = 4.dp
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Money Manager",
                    modifier = Modifier.padding(horizontal = 28.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 28.dp), color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))
                
                NavigationDrawerItem(
                    label = { Text("Goals", fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Routes.Goal)
                    },
                    icon = { Icon(Icons.Default.Flag, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("News & AI Tips", fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Routes.News)
                    },
                    icon = { Icon(Icons.Default.Newspaper, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("History", fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Routes.History)
                    },
                    icon = { Icon(Icons.Default.History, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            bottomBar = {
                if (showBottomNav) {
                    NavigationBar(
                        modifier = Modifier.fillMaxWidth().height(86.dp),
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        bottomNavItems.forEach { (screen, label) ->
                            val route = screen::class.qualifiedName!!
                            val isSelected = currentRoute == route
                            NavigationBarItem(
                                selected = isSelected,
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
                                    val icon = when (label) {
                                        "Home" -> Icons.Default.Home
                                        "Expenses" -> Icons.Default.StackedBarChart
                                        "Report" -> Icons.Default.Receipt
                                        "Profile" -> Icons.Default.Person
                                        else -> Icons.Default.Home
                                    }
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                },
                                label = {
                                    Text(
                                        text = label,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                )
                            )
                        }
                    }
                }
            }
        ) { paddingvalues ->
            Box(modifier = Modifier.padding(paddingvalues)) {

                NavHost(navController = navController, startDestination = Routes.Splash)
                {
                    composable<Routes.Splash> {
                        SplashScreen(navController = navController, firebaseAuth = firebaseAuth)
                    }
                    composable<Routes.Login> {
                        logInScreen(navController = navController)
                    }
                    composable<Routes.Goal> {
                        GoalScreen(navController = navController)
                    }

                    composable<Routes.SignUp> {
                        SignUpScrenn(navController = navController)


                    }
                    composable<Routes.Dashboard> {
                        HomeScreen(firebaseAuth = firebaseAuth, navcontroller = navController, onOpenDrawer = { scope.launch { drawerState.open() } })

                    }
                    composable<Routes.AddExpense> {
                        AddExpenseScreen(navController = navController, firebaseAuth = firebaseAuth)

                    }
                    composable<Routes.Report> {
                        ReportScreen(firebaseAuth = firebaseAuth)

                    }
                    composable<Routes.Profile> {
                        ProfileScreen(
                            navController = navController, firebaseAuth = firebaseAuth
                        )

                    }
                    composable<Routes.News> {
                        NewsScreen(navController = navController)
                    }
                    composable<Routes.History> {
                        HistoryScreen(navController = navController)
                    }

                }
            }
        }
    }
}

