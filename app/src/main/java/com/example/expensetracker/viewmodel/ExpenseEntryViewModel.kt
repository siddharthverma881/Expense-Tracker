package com.example.expensetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.local.entity.ExpenseEntity
import com.example.expensetracker.data.model.ExpenseCategory
import com.example.expensetracker.data.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ExpenseEntryViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    val totalSpentToday: StateFlow<Double> = repository
        .getTotalSpentOnDate(LocalDate.now())
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    fun addExpense(
        amount: Double,
        category: ExpenseCategory,
        description: String,
        title: String,
        date: Long
    ) {
        viewModelScope.launch {
            repository.addExpense(
                ExpenseEntity(
                    amount = amount,
                    category = category,
                    description = description,
                    title = title,
                    date = date
                )
            )
        }
    }
}
