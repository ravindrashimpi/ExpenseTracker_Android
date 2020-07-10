package com.deere.exptracker.util

import com.deere.exptracker.signup.FakeUserRepository
import com.deere.exptracker.signup.FakeUserViewModelFactory

object InjectorUtil {
    fun provideFakeUserViewModelFactory() : FakeUserViewModelFactory {
        val fakeUserRepository = FakeUserRepository.getInstance(FakeExpenseTrackerDB.getInstance().fakeUserDao)
        return FakeUserViewModelFactory(fakeUserRepository)
    }


}