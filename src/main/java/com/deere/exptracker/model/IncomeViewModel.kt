package com.deere.exptracker.model

import androidx.lifecycle.ViewModel
import com.deere.exptracker.entity.IncomeEntity
import com.deere.exptracker.repository.IncomeRepository

class IncomeViewModel(private val incomeRepository: IncomeRepository) : ViewModel() {

    suspend fun addIncome(income: IncomeEntity): Long {
        return incomeRepository.addIncome(income)
    }

    suspend fun checkForIncomeForCurrentMonth(pUserId: Int, pIncomeDate: String): IncomeEntity {
        return incomeRepository.checkForIncomeForCurrentMonth(pUserId, pIncomeDate)
    }

    suspend fun updateIncome(amount: Double, pUserId: Int): Int {
        return incomeRepository.updateIncome(amount, pUserId)
    }

    suspend fun deleteIncome(pUserId: Int, pIncomeDate: String) {
        incomeRepository.deleteIncome(pUserId, pIncomeDate)
    }

    suspend fun updateExpenseInIncome(pUserId: Int, newExpAmt: Double, pIncomeDate: String): Int {
        return incomeRepository.updateExpenseInIncome(pUserId, newExpAmt, pIncomeDate)
    }
}