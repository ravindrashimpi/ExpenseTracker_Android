package com.deere.exptracker.dashboard

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.deere.exptracker.R
import com.deere.exptracker.category.ListCategoryFragment
import com.deere.exptracker.chart.ChartFragment
import com.deere.exptracker.databinding.FragmentDashboardBinding
import com.deere.exptracker.entity.ExpenseCategoryEntity
import com.deere.exptracker.expense.ExpenseFragment
import com.deere.exptracker.expense.ExpenseList
import com.deere.exptracker.income.IncomeFragment
import com.deere.exptracker.login.LoginFragment
import com.deere.exptracker.util.SessionManagement
import com.google.android.material.navigation.NavigationView

class DashboardFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var sessionManagement: SessionManagement
    lateinit var binding: FragmentDashboardBinding
    lateinit var navController: NavController
    lateinit var categoryFragment: ListCategoryFragment
    lateinit var dashboardFragment: DashboardFragment
    lateinit var expenseFragment: ExpenseFragment
    lateinit var listExpenseFragment: ExpenseList
    lateinit var chartFragment: ChartFragment
    lateinit var loginFragment: LoginFragment
    lateinit var incomeFragment: IncomeFragment

    var TAG = "DashboardFragment"

    //Below variable is used to display the menu on top-left
    lateinit var drawerLayout: DrawerLayout
    lateinit var topNavView: NavigationView
    lateinit var toolBar: Toolbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView started")

        //Do the binding of the Fragment
        binding = FragmentDashboardBinding.inflate(inflater)

        //Inisitalize Session Management Object
        val application = requireNotNull(this.activity).application
        sessionManagement = SessionManagement(application.applicationContext)

        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_dashboard, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated started")

        /*----------------Hooks-------------------------*/
        drawerLayout = binding.drawerLayout
        topNavView = binding.topNavView
        toolBar = requireView().findViewById(R.id.myToolbar)

        /*----------------Tool Bar------------------*/
        (activity as AppCompatActivity).setSupportActionBar(toolBar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)


        /*----------------Navigation Drawer Menu------------------*/
        var toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(activity, drawerLayout, toolBar, R.string.open_drawer, R.string.close_drawer)
//        var toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(activity, drawerLayout, R.string.open_drawer, R.string.close_drawer)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        topNavView.setNavigationItemSelectedListener(this)

        //Initialize Navigation
        navController = Navigation.findNavController(view)
        //NavigationUI.setupActionBarWithNavController(activity as AppCompatActivity, navController)

        //Call the BottomNavigationMenu actions
        var userEntity = sessionManagement.getSession()
        if(userEntity != null) {
            chartFragment = ChartFragment()
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, chartFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        }

        var bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { item ->
                when(item) {
                    R.id.listCategoryFragment -> {
                            categoryFragment = ListCategoryFragment()
                            (activity as AppCompatActivity).supportActionBar?.setTitle("Categories")
                            requireActivity().supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.fragment_container, categoryFragment)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .addToBackStack(null)
                                .commit()
                        }
                    R.id.dashboardFragment -> {
                            chartFragment = ChartFragment()
                            (activity as AppCompatActivity).supportActionBar?.setTitle("Dashboard")
                            requireActivity().supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.fragment_container, chartFragment)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .addToBackStack(null)
                                .commit()
                        }
                    R.id.expenseFragment -> {
                            listExpenseFragment =
                                ExpenseList()
                            (activity as AppCompatActivity).supportActionBar?.setTitle("Expense List")
                            requireActivity().supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.fragment_container, listExpenseFragment)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .addToBackStack(null)
                                .commit()
                        }
                    R.id.incomeFragment -> {
                        incomeFragment = IncomeFragment()
                        (activity as AppCompatActivity).supportActionBar?.setTitle("Income")
                        requireActivity().supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment_container, incomeFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .addToBackStack(null)
                            .commit()
                    }
                    R.id.nav_signout -> {
                            sessionManagement.removeSession()
                            navController.navigate(R.id.loginFragment)
                        }
                }
            true
        }
//        var utils = Utils()
//        utils.initializeBottomNavigationView(view,navController)

     }

    override fun onStart() {

        super.onStart()

        //Check if user is logged in
        //If user is logged in continue to display the fragment
        var userEntity = sessionManagement.getSession()
        Log.d(TAG, "onStart: ${userEntity}")

        if(userEntity != null) {
            val headerView: View = topNavView.getHeaderView(0)
            val welcomeUserName: TextView = headerView.findViewById(R.id.welcomeId)
            welcomeUserName.text = userEntity.firstName + " " + userEntity.lastName
            Log.d(TAG, "onStart: ${headerView}")
        } else {
           navController.navigate(R.id.loginFragment)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                chartFragment = ChartFragment()
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, chartFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
            R.id.createCategory -> {
                categoryFragment = ListCategoryFragment()
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, categoryFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
            R.id.incomeFragment -> {

            }
            R.id.logout -> {
                sessionManagement.removeSession()
                navController.navigate(R.id.loginFragment)
            }
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        sessionManagement.removeSession()
    }


}


