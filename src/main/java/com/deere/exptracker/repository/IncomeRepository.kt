package com.deere.exptracker.repository

import com.deere.exptracker.DAO.IncomeDAO
import com.deere.exptracker.entity.IncomeEntity

class IncomeRepository(private val incomeDAO: IncomeDAO) {

   suspend fun addIncome(income: IncomeEntity): Long {
       return incomeDAO.addIncome(income)
   }

    suspend fun checkForIncomeForCurrentMonth(pUserId: Int, pIncomeDate: String): IncomeEntity {
        return incomeDAO.checkForIncomeForCurrentMonth(pUserId, pIncomeDate)
    }

    suspend fun updateIncome(amount: Double, pUserId: Int): Int {
        return incomeDAO.updateIncome(amount, pUserId)
    }

    suspend fun deleteIncome(pUserId: Int, pIncomeDate: String) {
        incomeDAO.deleteIncome(pUserId, pIncomeDate)
    }

    suspend fun updateExpenseInIncome(pUserId: Int, newExpAmt: Double, pIncomeDate: String): Int {
        return incomeDAO.updateExpenseInIncome(pUserId, newExpAmt, pIncomeDate)
    }
}