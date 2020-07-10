package com.deere.exptracker.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deere.exptracker.entity.CategoryEntity
import com.deere.exptracker.repository.CategoryRepository
import kotlinx.coroutines.launch

class CategoryViewModel(private val categoryRepository: CategoryRepository): ViewModel() {
    val TAG = "CategoryViewModel"

    fun addCategory(category: CategoryEntity): LiveData<Long> {
        var isCategoryAdded = MutableLiveData<Long>()
        viewModelScope.launch {
            isCategoryAdded.postValue(categoryRepository.addCategory(category))
            Log.d(TAG, "CategoryAdded: ${isCategoryAdded}")
        }
        return isCategoryAdded
    }

    fun listAllCategories(): LiveData<MutableList<CategoryEntity>> {
        var categories = MutableLiveData<MutableList<CategoryEntity>>()
        viewModelScope.launch {
            categories.postValue(categoryRepository.listAllCategory())
            Log.d(TAG, "ListCategories: ${categories}")
        }
        return categories
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(category)
        }
    }

}