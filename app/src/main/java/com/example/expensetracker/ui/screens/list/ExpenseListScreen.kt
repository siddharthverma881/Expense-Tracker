package com.example.expensetracker.ui.screens.list

import android.app.DatePickerDialog
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.expensetracker.R
import com.example.expensetracker.ui.components.LabeledText
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ExpenseListScreen(
    navController: NavController,
    viewModel: ExpenseListViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val groupedExpenses by viewModel.groupedExpenses.collectAsState()
    val groupBy by viewModel.groupBy.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val totalCount by viewModel.totalCount.collectAsState()
    val totalAmount by viewModel.totalAmount.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }

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
        // Date & Today Button Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Date: ${selectedDate.format(DateTimeFormatter.ofPattern("dd MMM yy"))}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = { showDatePicker = true }) {
                Icon(painterResource(id = R.drawable.ic_calendar), contentDescription = "Pick Date")
            }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = { viewModel.setSelectedDate(LocalDate.now()) }) {
                Text("Today")
            }
        }

        Spacer(Modifier.height(16.dp))

        // GroupBy toggle segmented buttons
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Group by: ", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.width(8.dp))
            SegmentedButtonToggle(
                options = listOf("Category", "Time"),
                selectedIndex = if (groupBy == ExpenseListViewModel.GroupBy.CATEGORY) 0 else 1,
                onSelected = { index ->
                    viewModel.setGroupBy(if (index == 0) ExpenseListViewModel.GroupBy.CATEGORY else ExpenseListViewModel.GroupBy.TIME)
                }
            )
        }

        Spacer(Modifier.height(16.dp))

        // Totals
        Text("Total Expenses: $totalCount", style = MaterialTheme.typography.bodyLarge)
        Text("Total Amount: ₹$totalAmount", style = MaterialTheme.typography.bodyLarge)

        Spacer(Modifier.height(16.dp))

        if (totalCount == 0) {
            Text("No expenses for selected date", style = MaterialTheme.typography.bodyLarge)
        } else {
            Crossfade(targetState = groupedExpenses, label = "ExpenseList") { expenses ->
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    expenses.forEach { (groupKey, expensesInGroup) ->
                        if (groupBy == ExpenseListViewModel.GroupBy.CATEGORY) {
                            item("header-$groupKey") {
                                Text(
                                    text = "Category: $groupKey",
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.padding(start = 0.dp, top = 8.dp, bottom = 0.dp, end = 8.dp)
                                )
                            }
                        }
                        items(
                            items = expensesInGroup,
                            key = { it.id } // assuming ExpenseEntity has unique id
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
                                            label = "Title",
                                            value = expense.title
                                        )
                                        LabeledText(
                                            label = "Amount",
                                            value = "₹${expense.amount}"
                                        )
                                        if (groupBy != ExpenseListViewModel.GroupBy.CATEGORY) {
                                            LabeledText(
                                                label = "Category",
                                                value = expense.category.name
                                            )
                                        }
                                    }
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
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}