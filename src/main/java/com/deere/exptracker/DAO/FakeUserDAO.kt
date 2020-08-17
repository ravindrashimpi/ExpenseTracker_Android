package com.deere.exptracker.DAO

import androidx.lifecycle.MutableLiveData
import com.deere.exptracker.entity.UserEntity

class FakeUserDAO {
    private val registerUsersList = mutableListOf<UserEntity>()
    private val registeredUsers = MutableLiveData<List<UserEntity>>()

    init {
        //registerUsersList.add(1, UserEntity(1,"Ravindra","Shimpi","ravindra.shimpi@gmail.com","Ravi@250249"))
        //registerUsersList.add(2, UserEntity(2,"Pranjali","Shimpi","pranjali.shimpi@gmail.com","Pranjali@250249"))
        registeredUsers.value = registerUsersList
    }

    fun registerUser(user: UserEntity) {
        registerUsersList.add(user)
        registeredUsers.value = registerUsersList
    }

    fun validateUser(emailId: String, password: String): Boolean {
        var flag: Boolean = false
        for (user in registerUsersList) {
            if (user.emailId == emailId && user.password == password)
                flag = true
        }
        return flag
    }
}