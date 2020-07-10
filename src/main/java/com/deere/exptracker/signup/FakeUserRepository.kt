package com.deere.exptracker.signup

import com.deere.exptracker.DAO.FakeUserDAO
import com.deere.exptracker.entity.UserEntity

class FakeUserRepository private constructor(private val userDao: FakeUserDAO) {

    fun registerUser(user: UserEntity) {
        userDao.registerUser(user)
    }

    fun validateUser(emailId: String, password: String): Boolean {
        return userDao.validateUser(emailId, password)
    }

    companion object {
        @Volatile private var instance: FakeUserRepository? = null

        fun getInstance(userDao: FakeUserDAO) =
            instance ?: synchronized(this) {
                instance ?: FakeUserRepository(userDao).also { instance = it }
            }
    }
}