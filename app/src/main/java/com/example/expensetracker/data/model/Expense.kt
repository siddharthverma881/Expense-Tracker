package com.example.expensetracker.data.model

import java.util.*

data class Expense(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val category: ExpenseCategory,
    val notes: String? = null,
    val date: Long = System.currentTimeMillis()
)