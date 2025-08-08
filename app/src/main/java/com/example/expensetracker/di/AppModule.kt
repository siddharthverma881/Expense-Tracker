package com.example.expensetracker.di

import android.content.Context
import androidx.room.Room
import com.example.expensetracker.ExpenseDatabase
import com.example.expensetracker.data.local.dao.ExpenseDao
import com.example.expensetracker.data.repository.ExpenseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

//    @Provides
//    @Singleton
//    fun provideDatabase(@ApplicationContext context: Context): ExpenseDatabase {
//        return Room.databaseBuilder(
//            context,
//            ExpenseDatabase::class.java,
//            "expense_db"
//        ).build()
//    }

//    @Provides
//    @Singleton
//    fun provideExpenseDao(db: ExpenseDatabase): ExpenseDao {
//        return db.expenseDao()
//    }

    @Provides
    @Singleton
    fun provideExpenseRepository(dao: ExpenseDao): ExpenseRepository {
        return ExpenseRepository(dao)
    }
}
