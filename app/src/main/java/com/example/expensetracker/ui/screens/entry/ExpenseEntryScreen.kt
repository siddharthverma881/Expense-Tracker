package com.example.expensetracker.ui.screens.entry

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun ExpenseEntryScreen(
    navController: NavController,
    viewModel: ExpenseEntryViewModel = hiltViewModel()
) {
    Text(text = "Expense Entry Screen")
}