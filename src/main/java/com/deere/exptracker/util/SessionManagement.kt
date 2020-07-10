package com.deere.exptracker.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.deere.exptracker.entity.UserEntity
import com.google.gson.Gson

class SessionManagement(context: Context) {
    val TAG = "SessionManagement"
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var gson: Gson
    var SHARED_PREFERENCE_NAME = "session"
    var SESSION_KEY = "session_user"

    init {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        gson = Gson()
    }

    fun saveSession(user: UserEntity) {
        //Save session of user whenever user is logged in
        var userEntity = gson.toJson(user)
        editor.putString(SESSION_KEY, userEntity).commit()
    }

    fun getSession(): UserEntity? {
        //Return emailId whose session is saved
        var user = sharedPreferences.getString(SESSION_KEY, null)
        var userEntity = gson.fromJson(user,UserEntity::class.java)
        return userEntity
    }

    fun removeSession() {
        editor.putString(SESSION_KEY, null).commit()
    }
}