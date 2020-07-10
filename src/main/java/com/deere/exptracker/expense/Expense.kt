package com.deere.exptracker.expense

import java.util.*

class Expense(val userId: String, val expDate: String, val amount: Double, val categoryId: String, val note: String) {}

object ExpenseData {
    val expenseList  = listOf<Expense>(
        Expense("1", "06/01/2020", 110.0,"2","Mobile Bill"),
        Expense("1", "06/02/2020",363.0,"3","Milk"),
        Expense("1", "06/03/2020",334.0,"4","Shopping for Anaya"),
        Expense("1", "06/03/2020",12.0,"1","Desert"),
        Expense("1", "06/03/2020",54.0,"11","General"),
        Expense("1", "06/04/2020",5.0,"6","Grocery"),
        Expense("1", "06/04/2020",415.0,"4","Electricity Bill"),
        Expense("1", "06/05/2020",432.0,"8","Dinner Out"),
        Expense("1", "06/05/2020",57.0,"8","Bread"),
        Expense("1", "06/09/2020",24.0,"2","Medical"),
        Expense("1", "06/09/2020",87.0,"4","Medical for Anaya"),
        Expense("1", "06/14/2020",50.0,"4","Pencil"),
        Expense("1", "06/14/2020",443.0,"5","Car Wash"),
        Expense("1", "06/14/2020",223.0,"5","D-Mart"),
        Expense("1", "06/19/2020",334.0,"7","Shopping"),
        Expense("1", "06/19/2020",2234.0,"7","Car Servicing"),
        Expense("1", "06/19/2020",53.0,"5","Milk"),
        Expense("1", "06/20/2020",75.0,"9","Bread"),
        Expense("1", "06/20/2020",84.0,"10","Eggs"),
        Expense("1", "06/20/2020",32.0,"3","Ice-Cream"),
        Expense("1", "06/20/2020",68.0,"1","General")
    )
}