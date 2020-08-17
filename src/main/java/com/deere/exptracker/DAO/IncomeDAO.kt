package com.deere.exptracker.DAO

import androidx.room.*
import com.deere.exptracker.entity.IncomeEntity

@Dao
interface IncomeDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addIncome(income: IncomeEntity): Long

    //    @Update
//    suspend fun updateIncome(income: IncomeEntity): Int
    @Query("UPDATE Income SET incomeAmt = :amount WHERE userId = :pUserId")
    suspend fun updateIncome(amount: Double, pUserId: Int): Int

    @Query("SELECT * FROM Income WHERE userId = :pUserId and strftime('%Y/%m',incomeDate) = :pIncomeDate")
    suspend fun checkForIncomeForCurrentMonth(pUserId: Int, pIncomeDate: String): IncomeEntity

    @Query("DELETE FROM Income WHERE userId = :pUserId and strftime('%Y/%m',incomeDate) = :pIncomeDate")
    suspend fun deleteIncome(pUserId: Int, pIncomeDate: String)

    @Query("UPDATE Income SET totalExpenseAmt = ((SELECT SUM(totalExpenseAmt) FROM Income WHERE userId = :pUserId) + :newExpAmt) WHERE userId = :pUserId and strftime('%Y/%m',incomeDate) = :pIncomeDate")
    suspend fun updateExpenseInIncome(pUserId: Int, newExpAmt: Double, pIncomeDate: String): Int
}