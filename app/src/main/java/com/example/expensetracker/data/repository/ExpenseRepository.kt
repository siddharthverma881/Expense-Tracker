package com.example.expensetracker.data.repository

import com.example.expensetracker.data.local.dao.ExpenseDao
import com.example.expensetracker.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExpenseRepository @Inject constructor(
    private val dao: ExpenseDao
) {
    suspend fun addExpense(expense: ExpenseEntity) = dao.insertExpense(expense)
    fun getAllExpenses(): Flow<List<ExpenseEntity>> = dao.getAllExpenses()
    suspend fun deleteExpense(id: Int) = dao.deleteExpense(id)
}
