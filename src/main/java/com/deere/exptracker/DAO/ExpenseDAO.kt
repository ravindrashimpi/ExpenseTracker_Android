package com.deere.exptracker.DAO

import androidx.room.*
import com.deere.exptracker.entity.ExpenseEntity

@Dao
interface ExpenseDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addExpense(expense: ExpenseEntity): Long

    @Query("SELECT * FROM Expense WHERE userId = :usrId and strftime('%Y/%m',expenseDate) = :expDate")
    suspend fun listAllExpense(usrId: Int, expDate: String): MutableList<ExpenseEntity>

    @Query("SELECT IFNULL(SUM(expenseAmt),0.0) as EXP_AMT from Expense WHERE userId = :usrId and strftime('%Y/%m',expenseDate) = :expDate")
    suspend fun getExpenseForMonth(usrId: Int, expDate: String): Double

    @Query("DELETE FROM Expense WHERE expenseId = :expId")
    suspend fun deleteExpense(expId: Int)

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Query("SELECT COUNT(1) as MY_COUNT FROM Expense WHERE expenseCategory = :categoryId")
    suspend fun checkForCategory(categoryId: Int): Int

    @Query("SELECT IFNULL(SUM(expenseAmt),0.0) FROM Expense WHERE userId = :pUserId and strftime('%m/%d',expenseDate) = :pExpDate")
    suspend fun getDailyExpense(pUserId: Int, pExpDate: String): Double
}