package com.example.expensetracker.ui.screens.entry

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.expensetracker.data.model.ExpenseCategory
import com.example.expensetracker.ui.components.DropdownMenuBox
import com.example.expensetracker.util.Utils.amountRegex
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseEntryScreen(
    navController: NavController,
    viewModel: ExpenseEntryViewModel = hiltViewModel()
) {
    // Local UI state
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(ExpenseCategory.Food) }
    var notes by remember { mutableStateOf("") }
    var receiptImageUri by remember { mutableStateOf<String?>(null) } // Mock image as URI string

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val totalSpentToday by viewModel.totalSpentToday.collectAsState()

    var animateAlpha by remember { mutableStateOf(1f) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Add Expense", style = MaterialTheme.typography.headlineSmall)

            Text(
                text = "Total Spent Today: ₹${"%.2f".format(totalSpentToday)}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.alpha(animateAlpha)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { input ->
                        if (input.matches(amountRegex)) {
                            amount = input
                        }
                    },
                    label = { Text("Amount (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                DropdownMenuBox(
                    selected = category,
                    onCategorySelected = { category = it }
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { if (it.length <= 100) notes = it },
                    label = { Text("Notes (optional)") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Mock Receipt Image upload button + preview
                Button(
                    onClick = {
                        // For now just toggle mock URI to simulate picking image
                        receiptImageUri = if (receiptImageUri == null) "mock_uri" else null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (receiptImageUri == null) "Add Receipt Image" else "Remove Receipt Image")
                }

                receiptImageUri?.let {
                    // Just a placeholder box representing the image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(top = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Receipt Image Preview (mock)", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (title.isNotBlank() && amount.isNotBlank()) {
                        coroutineScope.launch {
                            viewModel.addExpense(
                                amount = amount.toDoubleOrNull() ?: 0.0,
                                category = category,
                                description = notes,
                                title = title,
                                date = System.currentTimeMillis()
                            )
                            snackbarHostState.showSnackbar("Expense added")

                            // Animate fade out then clear form and fade in
                            animateAlpha = 0f
                            kotlinx.coroutines.delay(300)
                            title = ""
                            amount = ""
                            notes = ""
                            category = ExpenseCategory.Food
                            receiptImageUri = null
                            animateAlpha = 1f
                        }
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Please fill required fields")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
            }
        }
    }
}
