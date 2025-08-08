package com.example.expensetracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.ui.screens.entry.ExpenseEntryScreen
import com.example.expensetracker.ui.screens.list.ExpenseListScreen
import com.example.expensetracker.ui.screens.report.ExpenseReportScreen

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "entry") {
        composable("entry") { ExpenseEntryScreen(navController) }
        composable("list") { ExpenseListScreen(navController) }
        composable("report") { ExpenseReportScreen(navController) }
    }
}