package com.deere.exptracker.expense

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.deere.exptracker.DAO.CategoryDAO
import com.deere.exptracker.DAO.ExpenseDAO
import com.deere.exptracker.DAO.IncomeDAO
import com.deere.exptracker.R
import com.deere.exptracker.databinding.FragmentExpenseBinding
import com.deere.exptracker.entity.CategoryEntity
import com.deere.exptracker.entity.ExpenseCategoryEntity
import com.deere.exptracker.entity.ExpenseEntity
import com.deere.exptracker.entity.UserEntity
import com.deere.exptracker.model.*
import com.deere.exptracker.repository.CategoryRepository
import com.deere.exptracker.repository.ExpenseRepository
import com.deere.exptracker.repository.IncomeRepository
import com.deere.exptracker.util.ExpenseTrackerDB
import com.deere.exptracker.util.SessionManagement
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ExpenseFragment : Fragment(), View.OnClickListener,
    ExpenseCategoryAdapter.OnItemClickListner {

    var TAG = "ExpenseFragment"
    lateinit var binding: FragmentExpenseBinding
    lateinit var navController: NavController
    lateinit var categoryViewModel: CategoryViewModel
    lateinit var expenseViewModel: ExpenseViewModel
    lateinit var incomeViewModel: IncomeViewModel
    lateinit var adapter: ExpenseCategoryAdapter
    lateinit var listExpenseFragment: ExpenseList
    var selectedCategory: CategoryEntity? = null
    var isExpenseUpdate: Boolean = false
    var updateExpCatEntity: ExpenseCategoryEntity? = null
    lateinit var sessionManagement: SessionManagement
    lateinit var userEntity: UserEntity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Do the binding of the Fragment
        binding = FragmentExpenseBinding.inflate(inflater)

        //Inisitalize Session Management Object
        val application = requireNotNull(this.activity).application
        sessionManagement = SessionManagement(application.applicationContext)
        userEntity = sessionManagement.getSession()!!

        //Get data of Expense for update
        var bundleObj = arguments
        var gson = Gson()
        if (bundleObj != null) {
            updateExpCatEntity = gson.fromJson<ExpenseCategoryEntity>(
                (bundleObj.get("UPDATE_EXPENSE") as String),
                ExpenseCategoryEntity::class.java
            )
            isExpenseUpdate = bundleObj.getBoolean("IS_EXPENSE_UPDAT")
            setInputControls(updateExpCatEntity!!)

            //Display Update button and Hide Add Button
            binding.expenseAddBtn.text = "UPDATE"
        }

        //initialize Model and DB
        initializeModel()
        categoryViewModel.listAllCategories(userEntity.userId)
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer { categoryList ->
                adapter = ExpenseCategoryAdapter(
                    categoryList,
                    this,
                    (if (updateExpCatEntity != null) updateExpCatEntity!!.catId else 0)
                )
                binding.expCategoryRecyclerView.adapter = adapter
            })


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated started")

        //This will have reference to the Navigation Graph
        navController = Navigation.findNavController(view)

        //Add default date to Expense Date field
        if (!isExpenseUpdate) {
            binding.expDate.setText(formatDate())
        }

        //Handle BackButton event
//        val backBtnEvent = requireActivity().onBackPressedDispatcher.addCallback(this) {
//            Log.d(TAG, "onViewCreated BackButton initiated")
//            //navController!!.navigate(R.id.action_expenseFragment_to_dashboardFragment)
//        }

//        binding.expCancelBtn.setOnClickListener(this)
//        binding.expDoneBtn.setOnClickListener(this)
        binding.expDate.setOnClickListener(this)
        binding.cancelBTN.setOnClickListener(this)
        binding.expenseAddBtn.setOnClickListener(this)

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.expenseAddBtn -> {
                if (isExpenseUpdate) {
                    //Update the Expense
                    CoroutineScope(IO).launch {
                        updateExpense()
                        withContext(Main) {
                            //updateIncomeWithExpenseAmount()
                        }
                    }
                } else {
                    //Add the Expense
                    addExpense()
                    CoroutineScope(IO).launch {
                        //updateIncomeWithExpenseAmount()
                    }
                }
            }
            R.id.cancelBTN -> {
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
            R.id.expDate -> {
                Log.d(TAG, "Date Text Clicked: ${R.id.expDate}")
                val dateFormat = SimpleDateFormat("dd/MM/YYYY", Locale.US)
                //Calendar
                val cal = Calendar.getInstance()
                val year = cal.get(Calendar.YEAR)
                val month = cal.get(Calendar.MONTH)
                val day = cal.get(Calendar.DAY_OF_MONTH)

                val datePicker = DatePickerDialog(
                    requireContext(),
                    R.style.my_dialog_theme,
                    DatePickerDialog.OnDateSetListener { v, mYear, mMonth, mDay ->
                        val selectedDate = Calendar.getInstance()
                        selectedDate.set(Calendar.YEAR, mYear)
                        selectedDate.set(Calendar.MONTH, mMonth)
                        selectedDate.set(Calendar.DAY_OF_MONTH, mDay)
                        val date = dateFormat.format(selectedDate.time)
                        binding.expDate.setText(date)
                    },
                    year,
                    month,
                    day
                )
                datePicker.show()
            }
//            R.id.expCategory -> {
//                Log.d(TAG, "Category TextBox Clicked")
//                navController!!.navigate(R.id.action_expenseFragment_to_categoryFragment)
//            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "ExpenseFragment: onDestroyView")
    }

    /**
     * Method used to initialize the Model for Database
     */
    private fun initializeModel() {
        //Create a DAO Instance
        val application = requireNotNull(this.activity).application

        //Initialize CategoryDAO
        val categoryDao: CategoryDAO = ExpenseTrackerDB.getInstance(application).categoryDao
        val categoryRepository: CategoryRepository = CategoryRepository(categoryDao)
        val categoryViewModelFactory: CategoryViewModelFactory =
            CategoryViewModelFactory(categoryRepository)
        categoryViewModel =
            ViewModelProviders.of(this, categoryViewModelFactory).get(CategoryViewModel::class.java)

        //Initialize ExpenseDAO
        val expenseDao: ExpenseDAO = ExpenseTrackerDB.getInstance(application).expenseDao
        val expenseRepository: ExpenseRepository = ExpenseRepository(expenseDao)
        val expenseViewModelFactory: ExpenseViewModelFactory =
            ExpenseViewModelFactory(expenseRepository)
        expenseViewModel =
            ViewModelProviders.of(this, expenseViewModelFactory).get(ExpenseViewModel::class.java)

        //Initialize IncomeDAO
        val incomeDao: IncomeDAO = ExpenseTrackerDB.getInstance(application).incomeDao
        val incomeRepository: IncomeRepository = IncomeRepository(incomeDao)
        val incomeViewModelFactory: IncomeViewModelFactory =
            IncomeViewModelFactory(incomeRepository)
        incomeViewModel =
            ViewModelProviders.of(this, incomeViewModelFactory).get(IncomeViewModel::class.java)
    }

    override fun onClick(category: CategoryEntity) {
        Log.d(TAG, "onClick: ${category}")
        selectedCategory = category
    }

    /**
     * Method used to validate the input fields
     */
    private fun validateInputs(binding: FragmentExpenseBinding): Boolean {
        return when {
            binding.expDate.text.toString().isEmpty() -> {
                displayDialog("Warning", "Please select date.")
                //Toast.makeText(context, "Please select date.", Toast.LENGTH_SHORT).show();
                false
            }
            binding.expAmount.text.toString().isEmpty() -> {
                displayDialog("Warning", "Please enter valid amount.")
                //Toast.makeText(context, "Please enter valid amount.", Toast.LENGTH_SHORT).show();
                false
            }
            binding.expNote.text.toString().isEmpty() -> {
                displayDialog("Warning", "Please enter note.")
                //Toast.makeText(context, "Please enter note.", Toast.LENGTH_SHORT).show();
                false
            }
            selectedCategory == null -> {
                displayDialog("Warning", "Select Category from top.")
                //Toast.makeText(context, "Select Category from top.", Toast.LENGTH_SHORT).show();
                false
            }
            else -> {
                true
            }
        }
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
     * Method used to set the ExpenseFragment input controls
     */
    private fun setInputControls(expenseCategoryEntity: ExpenseCategoryEntity) {
        binding.expDate.setText(parseDate(expenseCategoryEntity.expDate.toString()))
        binding.expAmount.setText(expenseCategoryEntity.expAmount.toString())
        binding.expNote.setText(expenseCategoryEntity.expNote.toString())

        //Update the selectedCategory in case user don't want to update the existing category
        var tempCategory = CategoryEntity(
            expenseCategoryEntity.catId,
            expenseCategoryEntity.catName,
            expenseCategoryEntity.catImage,
            userEntity.userId
        )
        selectedCategory = tempCategory
    }

    /**
     * Date format
     */
    private fun formatDate(): String {
        var sdf: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        var cal = Calendar.getInstance()
        return sdf.format(cal.time)
    }


    /**
     * Date Parse
     */
    private fun parseDate(date: String): String {
        var sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd");
        var tempDate = sdf.parse(date)
        return SimpleDateFormat("dd/MM/yyyy").format(tempDate)
    }

    /**
     * Date Parse
     */
    private fun parseDateToDate(date: String): Date {
        var sdf: SimpleDateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
        var tempDate = sdf.parse(date)
        return tempDate
    }

    /**
     * Method used to Add Expense
     */
    private fun addExpense() {
        if (validateInputs(binding)) {
            var sdf = SimpleDateFormat("dd/MM/yyyy")
            var date = sdf.parse(binding.expDate.text.toString())
            var sdf1 = SimpleDateFormat("yyyy-MM-dd")
            var expenseEntity = ExpenseEntity(
                0,
                sdf1.format(date),
                binding.expAmount.text.toString().toDouble(),
                selectedCategory!!.caregoryId,
                binding.expNote.text.toString(),
                userEntity.userId
            )
            Log.d(TAG, "ExpenseObj: ${activity}")
            expenseViewModel.addExpense(expenseEntity)
                .observe(viewLifecycleOwner, Observer {
                    if (it != 0L) {
                        Toast.makeText(
                            context,
                            "Expenses Added in database ${it}.",
                            Toast.LENGTH_SHORT
                        ).show();
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
                })

        }
    }

    /**
     * Method used to update the Income table with Expense Amount
     */
    suspend fun updateIncomeWithExpenseAmount() {
        var sdf = SimpleDateFormat("dd/MM/yyyy")
        var date = sdf.parse(binding.expDate.text.toString())
        var sdf1 = SimpleDateFormat("yyyy/MM")
        var update = incomeViewModel.updateExpenseInIncome(
            userEntity.userId, binding.expAmount.text.toString().toDouble(), sdf1.format(
                date
            )
        )
        Log.d(TAG, "Update Income table: ${update}")
    }

    /**
     * Method used to update Expense
     */
    suspend private fun updateExpense() {
        var sdf = SimpleDateFormat("dd/MM/yyyy")
        var date = sdf.parse(binding.expDate.text.toString())
        var sdf1 = SimpleDateFormat("yyyy-MM-dd")
        var updateExpenseEntity = ExpenseEntity(
            updateExpCatEntity!!.expenseId,
            sdf1.format(date),
            binding.expAmount.text.toString().toDouble(),
            selectedCategory!!.caregoryId,
            binding.expNote.text.toString(),
            userEntity.userId
        )
        Log.d(TAG, "UPDATE EXPENSE: ${activity}")

        expenseViewModel.updateExpense(updateExpenseEntity)

        withContext(Main) {
            listExpenseFragment = ExpenseList()
            (activity as AppCompatActivity).supportActionBar?.setTitle("Expense List")
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, listExpenseFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sessionManagement.removeSession()
    }


}
