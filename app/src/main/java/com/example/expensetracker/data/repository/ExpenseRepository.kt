package com.example.expensetracker.data.repository

import com.example.expensetracker.data.model.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ExpenseRepository {
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>>
        get() = _expenses

    fun addExpense(expense: Expense) {
        _expenses.update { it + expense }
    }
}