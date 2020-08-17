package com.deere.exptracker.model

import android.util.Log
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deere.exptracker.entity.UserEntity
import com.deere.exptracker.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun validateUser(emailId: String, password: String): LiveData<UserEntity> {
        var userObj = MutableLiveData<UserEntity>();
        viewModelScope.launch {
            var user: UserEntity = userRepository.validateUser(emailId, password)
            Log.d("UserViewModel", "ValidateUser: ${user}")
            if (user != null) {
                userObj.postValue(user)
            } else {
                userObj.postValue(null)
            }
        }
        return userObj
    }

    fun registerUser(user: UserEntity) {
        viewModelScope.launch {
            userRepository.registerUser(user)
        }
    }

    fun deleteAllUser() {
        viewModelScope.launch {
            userRepository.deleteAllUser()
        }
    }
}