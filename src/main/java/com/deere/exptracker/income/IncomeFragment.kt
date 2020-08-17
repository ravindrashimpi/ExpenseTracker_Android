package com.deere.exptracker.income

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.deere.exptracker.DAO.IncomeDAO
import com.deere.exptracker.R
import com.deere.exptracker.dashboard.DashboardFragment
import com.deere.exptracker.databinding.FragmentIncomeBinding
import com.deere.exptracker.entity.IncomeEntity
import com.deere.exptracker.entity.UserEntity
import com.deere.exptracker.model.IncomeViewModel
import com.deere.exptracker.model.IncomeViewModelFactory
import com.deere.exptracker.repository.IncomeRepository
import com.deere.exptracker.util.ExpenseTrackerDB
import com.deere.exptracker.util.SessionManagement
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class IncomeFragment : Fragment(), View.OnClickListener {

    var TAG = "IncomeFragment"
    lateinit var binding: FragmentIncomeBinding
    lateinit var navController: NavController
    lateinit var sessionManagement: SessionManagement
    lateinit var userEntity: UserEntity
    lateinit var incomeViewModel: IncomeViewModel
    lateinit var dashboardFragment: DashboardFragment
    var incomeEntity: IncomeEntity? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "OnCreateView")
        //Do the binding of the Fragment
        binding = FragmentIncomeBinding.inflate(inflater)

        //Inisitalize Session Management Object
        val application = requireNotNull(this.activity).application
        sessionManagement = SessionManagement(application.applicationContext)
        userEntity = sessionManagement.getSession()!!

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        //This will have reference to the Navigation Graph
        navController = Navigation.findNavController(view)

        //initialize Model and DB
        initializeModel()

        //Check if Income if already added for the current month, if yet then
        //display the fragment for updaing the income.
        CoroutineScope(Dispatchers.IO).launch {
            incomeEntity = checkIncomeExist()
            if(incomeEntity != null) {
                withContext(Dispatchers.Main) {
                    binding.income.setText(incomeEntity!!.incomeAmt.toString())
                    binding.addIncome.text = "UPDATE"
                }
            }
        }

        //Perform Binding
        binding.amt1.setOnClickListener(this)
        binding.amt2.setOnClickListener(this)
        binding.amt3.setOnClickListener(this)
        binding.amt4.setOnClickListener(this)
        binding.amt5.setOnClickListener(this)
        binding.amt6.setOnClickListener(this)
        binding.addIncome.setOnClickListener(this)
        binding.cancelBTN.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.amt1 -> {binding.income.setText(binding.amt1.text)}
            R.id.amt2 -> {binding.income.setText(binding.amt2.text)}
            R.id.amt3 -> {binding.income.setText(binding.amt3.text)}
            R.id.amt4 -> {binding.income.setText(binding.amt4.text)}
            R.id.amt5 -> {binding.income.setText(binding.amt5.text)}
            R.id.amt6 -> {binding.income.setText(binding.amt6.text)}
            R.id.addIncome -> {
                if(validateInputs(binding)) {
                    if(incomeEntity != null) {
                        //Update Income
                        Log.d(TAG, "UPDATE INCOME")
                        CoroutineScope(Dispatchers.IO).launch {
                            var isUpdated = updateIncome()
                            if(isUpdated != -1) {
                                withContext(Dispatchers.Main) {
                                    displayDialog("Message", "Income updated for this month.")
                                    binding.addIncome.isEnabled = false
                                    binding.addIncome.setBackgroundResource(R.drawable.custom_btn_disable)
                                }
                            }
                        }
                    } else {
                        //Add Income
                        CoroutineScope(Dispatchers.IO).launch {
                            var isAdded = addIncome().toInt()
                            if (isAdded != -1) {
                                withContext(Dispatchers.Main) {
                                    displayDialog("Message", "Income added for this month.")
                                    binding.addIncome.isEnabled = false
                                    binding.addIncome.setBackgroundResource(R.drawable.custom_btn_disable)
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    displayDialog("Error", "Unable to add Income.")
                                }
                            }
                        }
                    }
                }
            }
            R.id.cancelBTN -> {

                var sdf = SimpleDateFormat("YYYY/MM")
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d(TAG, "Deleting Income: ${userEntity.userId} : ${sdf.format(Date())}")
                    incomeViewModel.deleteIncome(userEntity.userId, sdf.format(Date()))
                }
            }
        }
    }

    /**
     * Method used to add the Income for the user
     */
    suspend private fun addIncome(): Long {
        var sdf = SimpleDateFormat("yyyy-MM-dd")
        var incomeEntity = IncomeEntity(
                0,
                sdf.format(Date()),
                binding.income.text.toString().toDouble(),
                0.toDouble(),
                userEntity.userId
        )
        return incomeViewModel.addIncome(incomeEntity)
    }

    /**
     * Method used to Update income
     */
    suspend fun updateIncome(): Int {
        var sdf = SimpleDateFormat("yyyy-MM-dd")
        return incomeViewModel.updateIncome(binding.income.text.toString().toDouble(), userEntity.userId);
    }

    override fun onStart() {
        super.onStart()
       //Get the Loggedin User Details

        Log.d(TAG, "onStart: ${userEntity}")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ${userEntity}")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ${userEntity}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ${userEntity}")
        sessionManagement.removeSession()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: ${userEntity}")
    }

    /**
     * Method used to check if the income already exist for the logged in user
     */
    suspend fun checkIncomeExist() : IncomeEntity {
        var sdf = SimpleDateFormat("yyyy/MM")
        return incomeViewModel.checkForIncomeForCurrentMonth(userEntity.userId, sdf.format(Date()))
    }
    /**
     * Method used to initialize the Model for Database
     */
    private fun initializeModel() {
        //Create a DAO Instance
        val application = requireNotNull(this.activity).application

        //Initialize IncomeDAO
        val incomeDao: IncomeDAO = ExpenseTrackerDB.getInstance(application).incomeDao
        val incomeRepository: IncomeRepository = IncomeRepository(incomeDao)
        val incomeViewModelFactory: IncomeViewModelFactory = IncomeViewModelFactory(incomeRepository)
        incomeViewModel = ViewModelProviders.of(this, incomeViewModelFactory).get(IncomeViewModel::class.java)
    }

    /**
     * Method used to display Dialog
     */
    private fun displayDialog(title: String, message: String) {

        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(resources.getString(R.string.dialogOk)) { dialog, id -> }
            .show()
    }

    /**
     * Method used to validate the input fields
     */
    private fun validateInputs(binding: FragmentIncomeBinding): Boolean {
        return when {
            binding.income.text.toString().isEmpty() -> {
                displayDialog("Warning","Please enter income for the month.")
                false
            }
            else -> {
                true
            }
        }
    }


}
