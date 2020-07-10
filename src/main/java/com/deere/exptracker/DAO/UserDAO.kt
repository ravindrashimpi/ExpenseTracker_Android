package com.deere.exptracker.DAO

import androidx.lifecycle.LiveData
import androidx.room.*
import com.deere.exptracker.entity.UserEntity

@Dao
interface UserDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    //Use of SUSPEND modifier - Room database doesnot support access on the main thread because it might lock the UO for
    //the long period of time therefore we need to execute these functions in a background thread for that we can use
    //async task, but the most advance and easiest is Coroutines
    //The suspended fun is a fun that will post and get details in a later time just like AJAX Callback
    suspend fun registerUser(user: UserEntity): Long

    @Query("SELECT * FROM User WHERE emailId = :emailId and password = :password")
    //We don't have to use the SUSPEND modifier here because we are returning LiveData. So Room will execute this
    //function as a background thread
    suspend fun validateUser(emailId: String, password: String): UserEntity

    @Update
    suspend fun updateUser(user: UserEntity): Int

    @Delete
    suspend fun deleteUser(user: UserEntity): Int

    @Query("DELETE FROM User")
    suspend fun deleteAllUser()


}