package com.example.expensetracker.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.ui.screens.entry.ExpenseEntryScreen
import com.example.expensetracker.ui.screens.list.ExpenseListScreen
import com.example.expensetracker.ui.screens.report.ExpenseReportScreen
import com.example.expensetracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {

    val items = listOf(
        BottomNavItem("entry", "Entry", R.drawable.ic_add),
        BottomNavItem("list", "List", R.drawable.ic_list),
        BottomNavItem("report", "Report", R.drawable.ic_bar_chart)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "entry",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("entry") { ExpenseEntryScreen(navController) }
            composable("list") { ExpenseListScreen(navController) }
            composable("report") { ExpenseReportScreen(navController) }
        }
    }
}
