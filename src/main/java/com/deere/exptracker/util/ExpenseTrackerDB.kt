package com.deere.exptracker.util

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.deere.exptracker.DAO.CategoryDAO
import com.deere.exptracker.DAO.UserDAO
import com.deere.exptracker.entity.CategoryEntity
import com.deere.exptracker.entity.UserEntity

//Entities are used to define the Class that will tie with table.
//You can give as many as entity you want
@Database(entities = [UserEntity::class, CategoryEntity::class], version = 2, exportSchema = false)
abstract class ExpenseTrackerDB : RoomDatabase() {

    //All the DAO classes should be define here
    abstract val userDao : UserDAO
    abstract val categoryDao: CategoryDAO

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