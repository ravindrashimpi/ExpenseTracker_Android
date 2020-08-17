package com.deere.exptracker.repository

import com.deere.exptracker.DAO.CategoryDAO
import com.deere.exptracker.entity.CategoryEntity

class CategoryRepository(private val categoryDAO: CategoryDAO) {

    suspend fun addCategory(category: CategoryEntity): Long {
        return categoryDAO.addCategory(category)
    }

    suspend fun updateCategory(category: CategoryEntity): Int {
        return categoryDAO.updateCategory(category)
    }

    suspend fun deleteCategory(category: CategoryEntity): Int {
        return categoryDAO.deleteCategory(category)
    }

    suspend fun listAllCategory(pUserId: Int): MutableList<CategoryEntity> {
        return categoryDAO.listAllCategory(pUserId)
    }

    suspend fun getCategoryById(catId: Int, pUserId: Int): CategoryEntity {
        return categoryDAO.getCategoryById(catId, pUserId)
    }

    suspend fun isCategoryExist(pUserId: Int): Int {
        return categoryDAO.isCategoryExist(pUserId)
    }
}