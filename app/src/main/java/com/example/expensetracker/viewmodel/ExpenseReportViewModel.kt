package com.example.expensetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.local.entity.ExpenseEntity
import com.example.expensetracker.data.model.ExpenseCategory
import com.example.expensetracker.data.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class ExpenseReportViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    val expenses: StateFlow<List<ExpenseEntity>> = repository
        .getAllExpenses()
        .map { it.toList() } // ensures a new list instance every time
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyCategoryData: StateFlow<List<Map<ExpenseCategory, Double>>> =
        expenses.map { list ->
            val now = System.currentTimeMillis()
            val sevenDaysAgo = now - 6 * 24 * 60 * 60 * 1000
            val filtered = list.filter { it.date >= sevenDaysAgo }

            (0..6).map { dayOffset ->
                val dayMillis = now - (dayOffset * 24 * 60 * 60 * 1000)
                ExpenseCategory.values().associateWith { category ->
                    filtered.filter {
                        (it.date / (24 * 60 * 60 * 1000)) ==
                                (dayMillis / (24 * 60 * 60 * 1000)) &&
                                it.category == category
                    }.sumOf { it.amount }
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categoryTotals: StateFlow<Map<ExpenseCategory, Double>> =
        expenses.map { list ->
            val now = System.currentTimeMillis()
            val sevenDaysAgo = now - 6 * 24 * 60 * 60 * 1000
            val filtered = list.filter { it.date >= sevenDaysAgo }

            ExpenseCategory.values().associateWith { category ->
                filtered.filter { it.category == category }.sumOf { it.amount }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val dailyTotals: StateFlow<Map<LocalDate, Double>> = expenses
        .map { list ->
            list.groupBy { expense ->
                Instant.ofEpochMilli(expense.date)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            }
                .mapValues { (_, dayExpenses) ->
                    dayExpenses.sumOf { it.amount }
                }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())
}
