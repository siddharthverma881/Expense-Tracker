package com.example.expensetracker.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class ExpenseListViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    // Selected date state (default today)
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()

    // Grouping mode: by category or by time
    enum class GroupBy { CATEGORY, TIME }
    private val _groupBy = MutableStateFlow(GroupBy.TIME)
    val groupBy = _groupBy.asStateFlow()

    // All expenses from repo (could fetch all or filtered by date inside repo)
    private val allExpenses = repository.getAllExpenses()

    // Filter expenses by selected date
    val filteredExpenses = combine(allExpenses, selectedDate) { expenses, date ->
        expenses.filter { expense ->
            // Convert epoch millis to LocalDate using system default zone
            val expenseDate = Instant.ofEpochMilli(expense.date).atZone(ZoneId.systemDefault()).toLocalDate()
            expenseDate == date
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Total count of filtered expenses
    val totalCount = filteredExpenses.map { it.size }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    // Total amount of filtered expenses
    val totalAmount = filteredExpenses.map { expenses -> expenses.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun toggleGroupBy() {
        _groupBy.value = when (_groupBy.value) {
            GroupBy.TIME -> GroupBy.CATEGORY
            GroupBy.CATEGORY -> GroupBy.TIME
        }
    }

    fun setGroupBy(groupBy: GroupBy) {
        _groupBy.value = groupBy
    }

    // Group expenses according to groupBy mode
    val groupedExpenses = combine(filteredExpenses, groupBy) { expenses, groupBy ->
        when (groupBy) {
            GroupBy.CATEGORY -> expenses.groupBy { it.category }
            GroupBy.TIME -> expenses.groupBy { it.date }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())

    fun deleteExpense(id: Int) {
        viewModelScope.launch {
            repository.deleteExpense(id)
        }
    }
}
