package com.deere.exptracker.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deere.exptracker.entity.ExpenseEntity
import com.deere.exptracker.repository.ExpenseRepository
import kotlinx.coroutines.launch

class ExpenseViewModel(private val expeneRepository: ExpenseRepository) : ViewModel() {
    val TAG = "ExpenseViewModel"

    fun addExpense(expense: ExpenseEntity): MutableLiveData<Long> {
        var expenseAdded = MutableLiveData<Long>()
        viewModelScope.launch {
            expenseAdded.postValue(expeneRepository.addExpense(expense))
        }
        return expenseAdded
    }

    suspend fun listAllExpense(usrId: Int, expDate: String): MutableList<ExpenseEntity> {
        return expeneRepository.listAllExpense(usrId, expDate)
    }

    suspend fun deleteExpense(expID: Int) {
        expeneRepository.deleteExpense(expID)
    }

    suspend fun checkForCategory(categoryId: Int): Int {
        return expeneRepository.checkForCategory(categoryId)
    }

    suspend fun updateExpense(expense: ExpenseEntity) {
        expeneRepository.updateExpense(expense)
    }

    suspend fun getDailyExpense(pUserId: Int, pExpDate: String): Double {
        return expeneRepository.getDailyExpense(pUserId, pExpDate)
    }

    suspend fun getExpenseForMonth(usrId: Int, expDate: String): Double {
        return expeneRepository.getExpenseForMonth(usrId, expDate)
    }
}