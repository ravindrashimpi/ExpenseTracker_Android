package com.deere.exptracker.signup

import androidx.lifecycle.ViewModel
import com.deere.exptracker.entity.UserEntity

class FakeUserViewModel(private val fakeUserRepository: FakeUserRepository) : ViewModel() {

    fun validateUser(emailId: String, password: String): Boolean {
        return fakeUserRepository.validateUser(emailId, password)
    }

    fun registerUser(user: UserEntity) {
        fakeUserRepository.registerUser(user)
    }
}