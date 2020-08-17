package com.deere.exptracker.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FakeUserViewModelFactory(private val fakeUserRepository: FakeUserRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FakeUserViewModel(fakeUserRepository) as T
    }
}