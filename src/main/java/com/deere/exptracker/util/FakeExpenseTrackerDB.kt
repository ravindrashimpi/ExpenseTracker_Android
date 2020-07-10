package com.deere.exptracker.util

import com.deere.exptracker.DAO.FakeUserDAO

class FakeExpenseTrackerDB private constructor() {

    var fakeUserDao = FakeUserDAO()
        private set

    companion object {
        @Volatile private var instance: FakeExpenseTrackerDB? = null

        fun getInstance() =
                instance ?: synchronized(this) {
                    instance ?: FakeExpenseTrackerDB().also { instance = it }
                }
    }
}