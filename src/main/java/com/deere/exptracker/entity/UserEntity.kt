package com.deere.exptracker.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
data class UserEntity (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="userId")
    val userId: Int,

    @ColumnInfo(name="firstName")
    val firstName: String,

    @ColumnInfo(name="lastName")
    val lastName: String,

    @ColumnInfo(name="emailId")
    val emailId: String,

    @ColumnInfo(name="password")
    val password: String) {
}