package com.deere.exptracker.repository

import com.deere.exptracker.DAO.UserDAO
import com.deere.exptracker.entity.UserEntity

class UserRepository(private val userDao: UserDAO) {

    suspend fun validateUser(emailId: String, password: String): UserEntity {
        return userDao.validateUser(emailId, password)
    }

    suspend fun registerUser(userEntity: UserEntity): Long {
        return userDao.registerUser(userEntity)
    }

    suspend fun deleteAllUser() {
        userDao.deleteAllUser()
    }

//    companion object {
//        @Volatile private var instance: UserRepository? = null
//
//        fun getInstance(userDao: UserDAO) =
//            instance ?: synchronized(this) {
//                instance ?: UserRepository(userDao).also { instance = it }
//            }
//    }
}