package com.deere.exptracker.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.deere.exptracker.repository.ExpenseRepository

class ExpenseViewModelFactory(private val expenseRepository: ExpenseRepository) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            return ExpenseViewModel(expenseRepository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}