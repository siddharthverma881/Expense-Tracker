package com.example.expensetracker.di

import android.content.Context
import androidx.room.Room
import com.example.expensetracker.ExpenseDatabase
import com.example.expensetracker.data.local.dao.ExpenseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): ExpenseDatabase =
        Room.databaseBuilder(
            appContext,
            ExpenseDatabase::class.java,
            "expense_db"
        ).build()

    @Provides
    fun provideExpenseDao(db: ExpenseDatabase): ExpenseDao = db.expenseDao()
}
