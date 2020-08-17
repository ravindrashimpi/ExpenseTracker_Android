package com.deere.exptracker.entity

data class ExpenseCategoryEntity(
    var expenseId: Int,
    var catImage: String,
    var catName: String,
    var catId: Int,
    var expAmount: Double,
    var expDate: String,
    var expNote: String,
    var userId: Int
) {}