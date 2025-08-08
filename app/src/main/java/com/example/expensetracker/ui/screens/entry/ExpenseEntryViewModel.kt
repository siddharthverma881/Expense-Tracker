package com.example.expensetracker.ui.screens.entry

import androidx.lifecycle.ViewModel
import com.example.expensetracker.data.model.Expense
import com.example.expensetracker.data.model.ExpenseCategory
import com.example.expensetracker.data.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ExpenseEntryViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    val title = MutableStateFlow("")
    val amount = MutableStateFlow("")
    val category = MutableStateFlow(ExpenseCategory.Staff)
    val notes = MutableStateFlow("")

    fun submitExpense() {
        val amountValue = amount.value.toDoubleOrNull() ?: return
        if (title.value.isBlank() || amountValue <= 0) return

        val expense = Expense(
            title = title.value.trim(),
            amount = amountValue,
            category = category.value,
            notes = notes.value.takeIf { it.isNotBlank() }
        )
        repository.addExpense(expense)

        // Reset form
        title.value = ""
        amount.value = ""
        category.value = ExpenseCategory.Staff
        notes.value = ""
    }
}