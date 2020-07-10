package com.deere.exptracker.category

class Category(var caregoryId: String, var categoryName: String, var imgName: String) {}

object CategoryData {
    val categories = listOf<Category>(
        Category("1","Cloths", "ic_cloths"),
        Category("2","Eating Out", "ic_eating_out"),
        Category("3","Entertainment","ic_entertainment"),
        Category("4","Fuel","ic_fuel"),
        Category("5","General","ic_general"),
        Category("6","Gifts","ic_gift"),
        Category("7","Holidays","ic_holiday"),
        Category("8","Kids","ic_kids"),
        Category("9","Shopping","ic_shopping"),
        Category("10","Sports","ic_sports"),
        Category("11","Travel","ic_travel")
    )
}