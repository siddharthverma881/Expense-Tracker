package com.example.expensetracker.di

import com.example.expensetracker.data.local.dao.ExpenseDao
import com.example.expensetracker.data.repository.ExpenseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideExpenseRepository(dao: ExpenseDao): ExpenseRepository {
        return ExpenseRepository(dao)
    }
}
