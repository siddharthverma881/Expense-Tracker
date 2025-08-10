package com.example.expensetracker.ui.screens.list

import android.app.DatePickerDialog
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expensetracker.R
import com.example.expensetracker.ui.components.LabeledText
import com.example.expensetracker.viewmodel.ExpenseListViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Screen for viewing a list of expenses.
 * Features:
 * - Date picker to view past expenses
 * - Grouping by category or time
 * - Shows total count and amount for the day
 * - Allows deleting expenses
 */
@Composable
fun ExpenseListScreen(
    viewModel: ExpenseListViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Observed state from ViewModel
    val groupedExpenses by viewModel.groupedExpenses.collectAsState()
    val groupBy by viewModel.groupBy.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val totalCount by viewModel.totalCount.collectAsState()
    val totalAmount by viewModel.totalAmount.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }

    // Show date picker dialog when triggered
    if (showDatePicker) {
        val year = selectedDate.year
        val month = selectedDate.monthValue - 1
        val day = selectedDate.dayOfMonth

        LaunchedEffect(showDatePicker) {
            val dialog = DatePickerDialog(
                context,
                { _, y, m, d ->
                    viewModel.setSelectedDate(LocalDate.of(y, m + 1, d))
                    showDatePicker = false
                },
                year,
                month,
                day
            )
            dialog.setOnCancelListener {
                showDatePicker = false
            }
            dialog.show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Date selection row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Selected date display
            Text(
                text = "Date: ${selectedDate.format(DateTimeFormatter.ofPattern("dd MMM yy"))}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.width(8.dp))

            // Open date picker button
            IconButton(onClick = { showDatePicker = true }) {
                Icon(painterResource(id = R.drawable.ic_calendar), contentDescription = "Pick Date")
            }

            Spacer(Modifier.weight(1f))

            // Reset to today button
            TextButton(onClick = { viewModel.setSelectedDate(LocalDate.now()) }) {
                Text("Today")
            }
        }

        Spacer(Modifier.height(16.dp))

        // GroupBy selection
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Group by: ", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.width(8.dp))
            SegmentedButtonToggle(
                options = listOf("Category", "Time"),
                selectedIndex = if (groupBy == ExpenseListViewModel.GroupBy.CATEGORY) 0 else 1,
                onSelected = { index ->
                    viewModel.setGroupBy(
                        if (index == 0) ExpenseListViewModel.GroupBy.CATEGORY
                        else ExpenseListViewModel.GroupBy.TIME
                    )
                }
            )
        }

        Spacer(Modifier.height(16.dp))

        // Daily totals
        Text("Total Expenses: $totalCount", style = MaterialTheme.typography.bodyLarge)
        Text("Total Amount: ₹$totalAmount", style = MaterialTheme.typography.bodyLarge)

        Spacer(Modifier.height(16.dp))

        // Empty state
        if (totalCount == 0) {
            Text("No expenses for selected date", style = MaterialTheme.typography.bodyLarge)
        } else {
            // List of grouped expenses
            Crossfade(targetState = groupedExpenses, label = "ExpenseList") { expenses ->
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    expenses.forEach { (groupKey, expensesInGroup) ->
                        // Header for category grouping
                        if (groupBy == ExpenseListViewModel.GroupBy.CATEGORY) {
                            item("header-$groupKey") {
                                Text(
                                    text = "Category: $groupKey",
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.padding(start = 0.dp, top = 8.dp, bottom = 0.dp, end = 8.dp)
                                )
                            }
                        }

                        // Expense cards
                        items(
                            items = expensesInGroup,
                            key = { it.id } // Unique ID from ExpenseEntity
                        ) { expense ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        LabeledText(
                                            label = stringResource(id = R.string.list_expense_title_label),
                                            value = expense.title
                                        )
                                        LabeledText(
                                            label = stringResource(id = R.string.list_expense_amount_label),
                                            value = "₹${expense.amount}"
                                        )
                                        if (groupBy != ExpenseListViewModel.GroupBy.CATEGORY) {
                                            LabeledText(
                                                label = stringResource(id = R.string.list_expense_category_label),
                                                value = expense.category.name
                                            )
                                        }
                                    }

                                    // Delete expense button
                                    IconButton(
                                        onClick = { viewModel.deleteExpense(expense.id) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Expense",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Segmented button toggle for switching between options (e.g., Category/Time grouping).
 *
 * @param options List of option labels
 * @param selectedIndex Index of the currently selected option
 * @param onSelected Callback when an option is selected
 */
@Composable
fun SegmentedButtonToggle(
    options: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    Row {
        options.forEachIndexed { index, option ->
            val isSelected = index == selectedIndex
            Button(
                onClick = { onSelected(index) },
                colors = if (isSelected) {
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                } else {
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                shape = MaterialTheme.shapes.small,
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Text(
                    text = option,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}