package com.deere.exptracker.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.deere.exptracker.repository.IncomeRepository

class IncomeViewModelFactory (private val incomeRepository: IncomeRepository): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(IncomeViewModel::class.java)) {
            return IncomeViewModel(incomeRepository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}