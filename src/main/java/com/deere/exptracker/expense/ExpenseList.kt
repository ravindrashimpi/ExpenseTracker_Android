package com.deere.exptracker.expense

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.deere.exptracker.DAO.CategoryDAO
import com.deere.exptracker.DAO.ExpenseDAO
import com.deere.exptracker.DAO.IncomeDAO
import com.deere.exptracker.R

import com.deere.exptracker.databinding.FragmentExpenseListBinding
import com.deere.exptracker.entity.ExpenseCategoryEntity
import com.deere.exptracker.entity.ExpenseEntity
import com.deere.exptracker.entity.IncomeEntity
import com.deere.exptracker.entity.UserEntity
import com.deere.exptracker.model.*
import com.deere.exptracker.repository.CategoryRepository
import com.deere.exptracker.repository.ExpenseRepository
import com.deere.exptracker.repository.IncomeRepository
import com.deere.exptracker.util.ExpenseTrackerDB
import com.deere.exptracker.util.SessionManagement
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ExpenseList : Fragment(), View.OnClickListener, ExpenseListAdapter.OnItemSwipeListner {
    val TAG = "ExpenseList"
    lateinit var binding: FragmentExpenseListBinding
    lateinit var navController: NavController
    lateinit var expenseViewModel: ExpenseViewModel
    lateinit var categoryViewModel: CategoryViewModel
    lateinit var expenseFragment: ExpenseFragment
    lateinit var sessionManagement: SessionManagement
    lateinit var userEntity: UserEntity
    var incomeEntity: IncomeEntity? = null
    lateinit var incomeViewModel: IncomeViewModel
    lateinit var adapter: ExpenseListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Get a reference to the binding object and inflate the fragment views.
        binding = FragmentExpenseListBinding.inflate(inflater, container, false)

        //Inisitalize Session Management Object
        val application = requireNotNull(this.activity).application
        sessionManagement = SessionManagement(application.applicationContext)
        userEntity = sessionManagement.getSession()!!

        //Initialize Database
        initializeModel()

        setHasOptionsMenu(true)

        //Get the Expense List from the database using Coroutines
        CoroutineScope(IO).launch {
            setExpenseListWithView(resources)
        }

        ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(binding.expenseRecyclerView)

        //Set onClick Listner
        binding.addExpense.setOnClickListener(this)
        return binding.root
    }

    var itemTouchHelperCallBack: ItemTouchHelper.SimpleCallback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            TODO("Not yet implemented")
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            Log.d(TAG, "AdapterPosition: ${(binding.expenseRecyclerView.adapter as ExpenseListAdapter).getExpenseCategoryEntity(viewHolder.adapterPosition)}")
            var expenseCategoryObj = (binding.expenseRecyclerView.adapter as ExpenseListAdapter).getExpenseCategoryEntity(viewHolder.adapterPosition)
            CoroutineScope(IO).launch {
                deleteExpense(expenseCategoryObj.expenseId)
                setExpenseListWithView(resources)
            }
            //binding.expenseRecyclerView.adapter!!.notifyDataSetChanged()

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated started")

        //This will have reference to the Navigation Graph
        navController = Navigation.findNavController(view)

        //Check if Income if already added for the current month, if yet then
        //display the fragment for updaing the income.
        CoroutineScope(Dispatchers.IO).launch {
            incomeEntity = checkIncomeExist()
            if(incomeEntity == null) {
                withContext(Main) {
                    var sdf = SimpleDateFormat("MMMM YYYY")
                    binding.infoCardView.visibility = View.VISIBLE
                    binding.addExpense.isEnabled = false
                    binding.infoDetailMsg.setText("Please add the income before creating new Expense for ${sdf.format(Date())}")
                    binding.addExpense.setBackgroundResource(R.drawable.custom_circle_add_disable_btn)
                }
            } else if(isCategoryExist() == 0){
                withContext(Main) {
                    var sdf = SimpleDateFormat("MMMM YYYY")
                    binding.infoCardView.visibility = View.VISIBLE
                    binding.addExpense.isEnabled = false
                    binding.infoDetailMsg.setText("Please add the Category before creating new Expense for ${sdf.format(Date())}")
                    binding.addExpense.setBackgroundResource(R.drawable.custom_circle_add_disable_btn)
                }
            }
        }

        //Set the onClick listner for the SignIn and SignUp button
    }

    override fun onClick(v: View?) {
        Log.d(TAG, "onClick ${v!!.id}")
        when(v!!.id) {
            R.id.addExpense -> {
                expenseFragment = ExpenseFragment()
                (activity as AppCompatActivity).supportActionBar?.setTitle("Expense")
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, expenseFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    /**
     * Method used to set the ExpenseList on RecyclerViewAdapter
     */
    private suspend fun setExpenseListWithView(resources: Resources) {
        adapter = ExpenseListAdapter(getAllExpenses(), resources, this)

        withContext(Main) {
            binding.expenseRecyclerView.adapter = adapter
        }
    }

    /**
     * Method used to get the list of all expense
     */
    private suspend fun getAllExpenses(): ArrayList<ExpenseCategoryEntity> {
        var sdf = SimpleDateFormat("YYYY/MM")
        var expenseList =  expenseViewModel.listAllExpense(userEntity.userId, sdf.format(Date()))

        var expCatList = ArrayList<ExpenseCategoryEntity>()
        for(expenseEntity in expenseList) {
            var category = categoryViewModel.getCategoryById(expenseEntity.categoryId, userEntity.userId)
            var expCatEntity = ExpenseCategoryEntity(
                                             expenseEntity.expenseId,
                                    category.categoryImg,
                                    category.categoryName,
                                    expenseEntity.categoryId,
                                    expenseEntity.expenseAmt,
                                    expenseEntity.expenseDate,
                                    expenseEntity.expenseNote,
                                    userEntity.userId
                                )
            expCatList.add(expCatEntity)
        }
        return expCatList
    }

    /**
     * Method used to delete the Expense
     */
    private suspend fun deleteExpense(expId: Int) {
        expenseViewModel.deleteExpense(expId)

    }

    /**
     * Method used to initialize the Model for Database
     */
    private fun initializeModel() {
        //Create a DAO Instance
        val application = requireNotNull(this.activity).application

        //Create ExpenseDAO
        val expenseDao: ExpenseDAO = ExpenseTrackerDB.getInstance(application).expenseDao
        val expenseRepository: ExpenseRepository = ExpenseRepository(expenseDao)
        val expenseViewModelFactory: ExpenseViewModelFactory = ExpenseViewModelFactory(expenseRepository)
        expenseViewModel = ViewModelProviders.of(this, expenseViewModelFactory).get(ExpenseViewModel::class.java)

        //Create CategoryDAO
        val categoryDao: CategoryDAO = ExpenseTrackerDB.getInstance(application).categoryDao
        val categoryRepository: CategoryRepository = CategoryRepository(categoryDao)
        val categoryViewModelFactory: CategoryViewModelFactory = CategoryViewModelFactory(categoryRepository)
        categoryViewModel = ViewModelProviders.of(this, categoryViewModelFactory).get(CategoryViewModel::class.java)

        //Create IncomeDAO
        val incomeDao: IncomeDAO = ExpenseTrackerDB.getInstance(application).incomeDao
        val incomeRepository: IncomeRepository = IncomeRepository(incomeDao)
        val incomeViewModelFactory: IncomeViewModelFactory = IncomeViewModelFactory(incomeRepository)
        incomeViewModel = ViewModelProviders.of(this, incomeViewModelFactory).get(IncomeViewModel::class.java)
    }

    override fun deleteItemOnSwipe(expenseCategoryEntity: ExpenseCategoryEntity) {
        Log.d(TAG, "DeleteExpense: ${expenseCategoryEntity}")
    }


    /**
     * Method used to redirect to the ExpenseFragment for update the Expense
     */
    private fun callUpdateExpenseFragment(updateFlag: Boolean, expenseCategoryEntity: ExpenseCategoryEntity) {
        var title: String = "";
        var btnLabel: String = "";
        if(updateFlag) {
            title = "Update Expense"
            btnLabel = "UPDATE"
        }
        else {
            title = "Expense"
            btnLabel = "ADD"
        }

        var bundle = Bundle()
        var gson = Gson()
        var expCatJSON = gson.toJson(expenseCategoryEntity)
        bundle.putString("UPDATE_EXPENSE", expCatJSON)
        bundle.putBoolean("IS_EXPENSE_UPDAT", true)

        expenseFragment = ExpenseFragment()
        expenseFragment.arguments = bundle
        (activity as AppCompatActivity).supportActionBar?.setTitle(title)
        requireActivity().supportFragmentManager
            .beginTransaction()
            //.replace(R.id.fragment_container, expenseFragment, bundle)
            .replace(R.id.fragment_container,expenseFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)

            .commit()
    }

    override fun getUpdateExpenseFragmentView(updateFlag: Boolean, expenseCategoryEntity: ExpenseCategoryEntity) {
        callUpdateExpenseFragment(updateFlag, expenseCategoryEntity)
    }

    /**
     * Method used to check if the income already exist for the logged in user
     */
    suspend fun checkIncomeExist() : IncomeEntity {
        var sdf = SimpleDateFormat("yyyy/MM")
        return incomeViewModel.checkForIncomeForCurrentMonth(userEntity.userId, sdf.format(Date()))
    }

    /**
     * Method used to check if the category already exist for the logged in user
     */
    suspend fun isCategoryExist(): Int {
        return categoryViewModel.isCategoryExist(userEntity.userId)
    }

    override fun onDestroy() {
        super.onDestroy()
        sessionManagement.removeSession()
    }

    /**
     * Method used for Expense Search
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.top_right_menu, menu)

        var item = menu.findItem(R.id.action_search)
        var searchView = item.actionView as SearchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })

        Log.d(TAG, "onCreateOptionsMenu")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onOptionsItemSelected")
        exportData()
        return super.onOptionsItemSelected(item)
    }

    private fun exportData() {
        var data = StringBuilder()
        data.append("Time, Distance")
        for(i in 0 until 10) {
            var d = i.toString() + "," + (i*i) + "\n";
            data.append(d)
        }
        Log.d(TAG, "DATA: ${data}" )

        var fileWriter: FileWriter? = null
        try {
            fileWriter = FileWriter("Expense.csv")
            fileWriter.append(data)

            Log.d(TAG, "FILE WRITTEN DONE.")

            //Exporting the file
            var context = context

        } catch(e: Exception) {
            Log.d(TAG, "EXCEPTION: ${e.message}")
        } finally {
            try {
                fileWriter!!.flush()
                fileWriter.close()
            } catch (e: IOException) {
                Log.d(TAG, "IOEXCEPTION: ${e.message}")
            }
        }
    }

}
