package com.deere.exptracker.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Income",
    indices = arrayOf(Index(value = ["incomeDate", "incomeId"], unique = true))
)
data class IncomeEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "incomeId")
    val incomeId: Int,

    @ColumnInfo(name = "incomeDate")
    val incomeDate: String,

    @ColumnInfo(name = "incomeAmt")
    val incomeAmt: Double,

    @ColumnInfo(name = "totalExpenseAmt")
    val totalExpAmt: Double,

    @ColumnInfo(name = "userId")
    val userId: Int
) {
}