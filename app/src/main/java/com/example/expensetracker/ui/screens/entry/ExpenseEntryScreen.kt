package com.example.expensetracker.ui.screens.entry

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.expensetracker.ui.components.DropdownMenuBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseEntryScreen(
    navController: NavController,
    viewModel: ExpenseEntryViewModel = hiltViewModel()
) {
    val title by viewModel.title.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val category by viewModel.category.collectAsState()
    val notes by viewModel.notes.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Add Expense", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = title,
            onValueChange = { viewModel.title.value = it },
            label = { Text("Title") }
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { viewModel.amount.value = it },
            label = { Text("Amount (₹)") }
        )

        DropdownMenuBox(category, onCategorySelected = {
            viewModel.category.value = it
        })

        OutlinedTextField(
            value = notes,
            onValueChange = { viewModel.notes.value = it },
            label = { Text("Notes (optional)") },
            maxLines = 2
        )

        Button(
            onClick = {
                viewModel.submitExpense()
//                navController.navigate("list")
            }
        ) {
            Text("Submit")
        }
    }
}
