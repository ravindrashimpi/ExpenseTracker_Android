package com.deere.exptracker.entity

import java.util.*

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