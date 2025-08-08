package com.example.expensetracker.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.expensetracker.data.model.ExpenseCategory

val CategoryColors: Map<ExpenseCategory, Color> = mapOf(
    ExpenseCategory.Food to Color(0xFFE57373),      // Red
    ExpenseCategory.Travel to Color(0xFF64B5F6), // Blue
    ExpenseCategory.Staff to Color(0xFF81C784),  // Green
    ExpenseCategory.Utility to Color(0xFFFFB74D), // Orange
)
