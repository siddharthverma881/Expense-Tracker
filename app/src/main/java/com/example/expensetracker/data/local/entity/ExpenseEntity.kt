package com.example.expensetracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.expensetracker.data.model.ExpenseCategory
import java.util.*

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val category: ExpenseCategory,
    val description: String,
    val title: String,
    val date: Long // store as timestamp for easy sorting
)
