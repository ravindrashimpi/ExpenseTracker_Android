package com.deere.exptracker.DAO

import androidx.room.*
import com.deere.exptracker.entity.CategoryEntity

@Dao
interface CategoryDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCategory(category: CategoryEntity): Long

//    @Update
//    suspend fun updateCategory(category: CategoryEntity): Int
//
    @Delete
    suspend fun deleteCategory(category: CategoryEntity): Int

    @Query("SELECT * FROM Category")
    suspend fun listAllCategory(): MutableList<CategoryEntity>
}