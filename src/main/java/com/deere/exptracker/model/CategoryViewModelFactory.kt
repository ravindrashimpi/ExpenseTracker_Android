package com.deere.exptracker.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.deere.exptracker.repository.CategoryRepository

class CategoryViewModelFactory(private val categoryRepository: CategoryRepository): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            return CategoryViewModel(categoryRepository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}