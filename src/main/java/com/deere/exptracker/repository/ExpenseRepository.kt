package com.deere.exptracker.repository

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deere.exptracker.DAO.ExpenseDAO
import com.deere.exptracker.entity.ExpenseEntity
import kotlin.math.exp

class ExpenseRepository(private val expenseDAO: ExpenseDAO) {

    suspend fun addExpense(expense: ExpenseEntity): Long {
        return expenseDAO.addExpense(expense)
    }

    suspend fun listAllExpense(usrId: Int, expDate: String): MutableList<ExpenseEntity> {
        return expenseDAO.listAllExpense(usrId, expDate)
    }

    suspend fun deleteExpense(expId: Int) {
        expenseDAO.deleteExpense(expId)
    }

    suspend fun updateExpense(expense: ExpenseEntity) {
        expenseDAO.updateExpense(expense)
    }

    suspend fun checkForCategory(categoryId: Int): Int {
        return expenseDAO.checkForCategory(categoryId)
    }

    suspend fun getDailyExpense(pUserId: Int, pExpDate: String): Double {
        return expenseDAO.getDailyExpense(pUserId, pExpDate)
    }

    suspend fun getExpenseForMonth(usrId: Int, expDate: String): Double {
        return expenseDAO.getExpenseForMonth(usrId, expDate)
    }
}