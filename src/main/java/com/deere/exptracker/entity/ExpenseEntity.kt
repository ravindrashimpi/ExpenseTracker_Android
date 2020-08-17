package com.deere.exptracker.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "Expense", indices = arrayOf(Index(value = ["expenseDate", "userId", "expenseId"], unique = true)))
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="expenseId")
    val expenseId: Int,

    @ColumnInfo(name="expenseDate")
    val expenseDate: String,

    @ColumnInfo(name="expenseAmt")
    val expenseAmt: Double,

    @ColumnInfo(name="expenseCategory")
    val categoryId: Int,

    @ColumnInfo(name="expenseNote")
    val expenseNote: String,

    @ColumnInfo(name="userId")
    val userId: Int
) {}