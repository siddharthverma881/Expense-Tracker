package com.example.expensetracker.util

import androidx.room.TypeConverter
import com.example.expensetracker.data.model.ExpenseCategory

class Converters {

    @TypeConverter
    fun fromCategory(category: ExpenseCategory): String = category.name

    @TypeConverter
    fun toCategory(name: String): ExpenseCategory = ExpenseCategory.valueOf(name)
}
