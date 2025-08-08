package com.example.expensetracker.ui.screens.report

import androidx.lifecycle.ViewModel
import com.example.expensetracker.data.model.ExpenseCategory
import com.example.expensetracker.data.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ExpenseReportViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    val expenses: StateFlow<List<com.example.expensetracker.data.model.Expense>> = repository.expenses

    fun getTotalForLast7Days(): Map<String, Double> {
        val now = System.currentTimeMillis()
        val sevenDaysAgo = now - 6 * 24 * 60 * 60 * 1000
        val filtered = expenses.value.filter { it.date >= sevenDaysAgo }

        return (0..6).associate { dayOffset ->
            val dayMillis = now - (dayOffset * 24 * 60 * 60 * 1000)
            val dayLabel = "Day ${7 - dayOffset}"
            val total = filtered.filter {
                (it.date / (24 * 60 * 60 * 1000)) ==
                        (dayMillis / (24 * 60 * 60 * 1000))
            }.sumOf { it.amount }
            dayLabel to total
        }
    }

    fun getCategoryTotals(): Map<ExpenseCategory, Double> {
        val now = System.currentTimeMillis()
        val sevenDaysAgo = now - 6 * 24 * 60 * 60 * 1000
        val filtered = expenses.value.filter { it.date >= sevenDaysAgo }

        return ExpenseCategory.values().associateWith { category ->
            filtered.filter { it.category == category }.sumOf { it.amount }
        }
    }

    fun getDailyCategoryTotals(): List<Map<ExpenseCategory, Double>> {
        val now = System.currentTimeMillis()
        val sevenDaysAgo = now - 6 * 24 * 60 * 60 * 1000
        val filtered = expenses.value.filter { it.date >= sevenDaysAgo }

        return (0..6).map { dayOffset ->
            val dayMillis = now - (dayOffset * 24 * 60 * 60 * 1000)
            val dailyMap = ExpenseCategory.values().associateWith { category ->
                filtered.filter {
                    (it.date / (24 * 60 * 60 * 1000)) ==
                            (dayMillis / (24 * 60 * 60 * 1000)) &&
                            it.category == category
                }.sumOf { it.amount }
            }
            dailyMap
        }
    }

}
