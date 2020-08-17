package com.deere.exptracker.util

import android.content.Context
import androidx.room.*
import com.deere.exptracker.DAO.CategoryDAO
import com.deere.exptracker.DAO.ExpenseDAO
import com.deere.exptracker.DAO.IncomeDAO
import com.deere.exptracker.DAO.UserDAO
import com.deere.exptracker.entity.CategoryEntity
import com.deere.exptracker.entity.ExpenseEntity
import com.deere.exptracker.entity.IncomeEntity
import com.deere.exptracker.entity.UserEntity

//Entities are used to define the Class that will tie with table.
//You can give as many as entity you want
@Database(entities = [UserEntity::class, CategoryEntity::class, ExpenseEntity::class, IncomeEntity::class], version = 24, exportSchema = false)
@TypeConverters(DateConverters::class)
abstract class ExpenseTrackerDB : RoomDatabase() {

    //All the DAO classes should be define here
    abstract val userDao : UserDAO
    abstract val categoryDao: CategoryDAO
    abstract val expenseDao: ExpenseDAO
    abstract val incomeDao: IncomeDAO

    //COMPANION OBJECT is another way of creating a Singleton class object
    companion object {
        @Volatile
        private var INSTANCE: ExpenseTrackerDB? = null

        fun getInstance(context: Context): ExpenseTrackerDB {
            synchronized(this) {
                var instance = INSTANCE

                if(instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext,
                                                    ExpenseTrackerDB::class.java,
                                                "EXPENSE_TRACKER_DB")
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}