package com.deere.exptracker.DAO

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.deere.exptracker.entity.CategoryEntity

@Dao
interface CategoryDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCategory(category: CategoryEntity): Long

    @Update
    suspend fun updateCategory(category: CategoryEntity): Int

    @Delete
    suspend fun deleteCategory(category: CategoryEntity): Int

    @Query("SELECT * FROM Category WHERE userId = :pUserId")
    suspend fun listAllCategory(pUserId: Int): MutableList<CategoryEntity>

    @Query("SELECT * FROM Category WHERE categoryId = :catId AND userId = :pUserId")
    suspend fun getCategoryById(catId: Int, pUserId: Int): CategoryEntity

    @Query("SELECT COUNT(1) MYCOUNT FROM Category WHERE userId = :pUserId")
    suspend fun isCategoryExist(pUserId: Int): Int
}