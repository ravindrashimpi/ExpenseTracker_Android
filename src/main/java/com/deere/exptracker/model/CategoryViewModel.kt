package com.deere.exptracker.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deere.exptracker.entity.CategoryEntity
import com.deere.exptracker.repository.CategoryRepository
import kotlinx.coroutines.launch

class CategoryViewModel(private val categoryRepository: CategoryRepository) : ViewModel() {
    val TAG = "CategoryViewModel"

    suspend fun addCategory(category: CategoryEntity): Long {
        return categoryRepository.addCategory(category)
    }

    fun listAllCategories(pUserId: Int): LiveData<MutableList<CategoryEntity>> {
        var categories = MutableLiveData<MutableList<CategoryEntity>>()
        viewModelScope.launch {
            categories.postValue(categoryRepository.listAllCategory(pUserId))
            Log.d(TAG, "ListCategories: ${categories}")
        }
        return categories
    }

    suspend fun deleteCategory(category: CategoryEntity): Int {
        return categoryRepository.deleteCategory(category)
    }

    suspend fun getCategoryById(catId: Int, pUserId: Int): CategoryEntity {
        return categoryRepository.getCategoryById(catId, pUserId)
    }

    suspend fun updateCategory(categoryEntity: CategoryEntity): Int {
        return categoryRepository.updateCategory(categoryEntity)
    }

    suspend fun isCategoryExist(pUserId: Int): Int {
        return categoryRepository.isCategoryExist(pUserId)
    }
}