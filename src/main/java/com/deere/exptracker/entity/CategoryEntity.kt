package com.deere.exptracker.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Category")
data class CategoryEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="categoryId")
    val caregoryId: Int,

    @ColumnInfo(name="categoryName")
    val categoryName: String,

    @ColumnInfo(name="categoryImg")
    val categoryImg: String
){}