package com.deere.exptracker.util

import android.view.View
import androidx.navigation.NavController
import com.deere.exptracker.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class Utils {
    lateinit var sessionManagement: SessionManagement

    public fun initializeBottomNavigationView(view: View, navController: NavController) {
        //Initialize BottomNavigationView
        var bottomNavigationView: BottomNavigationView? = null
        bottomNavigationView = view.findViewById(R.id.bottomNavigationView)
        bottomNavigationView!!.setOnNavigationItemSelectedListener{ item ->
            when(item.itemId) {
//                R.id.expenseFragment -> navController.navigate(R.id.action_dashboardFragment_to_expenseFragment)
//                R.id.dashboardFragment -> navController.navigate(R.id.dashboardFragment)
//                R.id.categoryFragment -> navController.navigate(R.id.action_dashboardFragment_to_categoryFragment)
//                R.id.nav_Category -> navController.navigate(R.id.action_dashboardFragment_to_expenseList)
//                R.id.nav_signout -> {
//                    sessionManagement = SessionManagement(view.context)
//                    sessionManagement.removeSession()
//                    navController.navigate(R.id.action_dashboardFragment_to_loginFragment)
//                }
            }
            true
        }
    }
}