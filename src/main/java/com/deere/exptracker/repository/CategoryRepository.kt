package com.deere.exptracker.repository

import com.deere.exptracker.DAO.CategoryDAO
import com.deere.exptracker.entity.CategoryEntity

class CategoryRepository(private val categoryDAO: CategoryDAO) {

    suspend fun addCategory(category: CategoryEntity): Long {
        return categoryDAO.addCategory(category)
    }

//    suspend fun updateCategory(category: CategoryEntity): Int {
//        return categoryDAO.updateCategory(category)
//    }
//
    suspend fun deleteCategory(category: CategoryEntity): Int {
        return categoryDAO.deleteCategory(category)
    }

    suspend fun listAllCategory(): MutableList<CategoryEntity> {
        return categoryDAO.listAllCategory()
    }
}