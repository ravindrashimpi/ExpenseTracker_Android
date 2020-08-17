package com.deere.exptracker.category

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deere.exptracker.DAO.CategoryDAO
import com.deere.exptracker.DAO.ExpenseDAO
import com.deere.exptracker.R
import com.deere.exptracker.databinding.FragmentCategoryBinding
import com.deere.exptracker.entity.CategoryEntity
import com.deere.exptracker.entity.UserEntity
import com.deere.exptracker.model.CategoryViewModel
import com.deere.exptracker.model.CategoryViewModelFactory
import com.deere.exptracker.model.ExpenseViewModel
import com.deere.exptracker.model.ExpenseViewModelFactory
import com.deere.exptracker.repository.CategoryRepository
import com.deere.exptracker.repository.ExpenseRepository
import com.deere.exptracker.util.ExpenseTrackerDB
import com.deere.exptracker.util.IconFragment
import com.deere.exptracker.util.SessionManagement
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ListCategoryFragment : Fragment(), View.OnClickListener, CategoryAdapter.OnListInteractionListener {
    var TAG = "ListCategoryFragment"
    lateinit var navController: NavController
    lateinit var binding: FragmentCategoryBinding
    lateinit var newCategoryFragment: NewCategoryFragment
    lateinit var iconFragment: IconFragment
    lateinit var categoryViewModel: CategoryViewModel
    lateinit var expenseViewModel: ExpenseViewModel
    lateinit var adapter: CategoryAdapter
    lateinit var gridLayoutManager: RecyclerView.LayoutManager
    lateinit var sessionManagement: SessionManagement
    lateinit var userEntity: UserEntity

    val TARGET_FRAGMENT_REQUEST_CODE = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView started")
        // Get a reference to the binding object and inflate the fragment views.
        binding = FragmentCategoryBinding.inflate(inflater, container, false)

        //Inisitalize Session Management Object
        val application = requireNotNull(this.activity).application
        sessionManagement = SessionManagement(application.applicationContext)
        userEntity = sessionManagement.getSession()!!

        //initialize Model and DB
        initializeModel()
        gridLayoutManager = GridLayoutManager(context, 2)
        categoryViewModel.listAllCategories(userEntity.userId).observe(viewLifecycleOwner, Observer { categoriesList ->
            Log.d(TAG, "onCreateView: ListOfCategories: ${categoriesList.size}")
            adapter = CategoryAdapter(categoriesList, false, this)
            Log.d(TAG, "onCreateView: Adapter: ${adapter}")
            binding.categoryRecyclerView.layoutManager = gridLayoutManager
            binding.categoryRecyclerView.adapter = adapter
            binding.categoryRecyclerView.setHasFixedSize(true)
        })

        //App/Back Button


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //This will have reference to the Navigation Graph
        navController = Navigation.findNavController(view)

        //Initialized Click Listner
        binding.addCategory.setOnClickListener(this)
        //binding.editCategoryLnk.setOnClickListener(this)
    }

    @SuppressLint("ResourceType")
    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.addCategory -> {
//                newCategoryFragment = NewCategoryFragment()
//                requireActivity().supportFragmentManager
//                    .beginTransaction()
//                    .add(R.id.fragment_container, newCategoryFragment)
//                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                    .addToBackStack(null)
//                    .commit()
                newCategoryFragment = NewCategoryFragment()
                newCategoryFragment.setTargetFragment(this, TARGET_FRAGMENT_REQUEST_CODE)
                var ft = requireActivity().supportFragmentManager.beginTransaction()
                newCategoryFragment.show(ft, "NewCaegoryFragment")

//                iconFragment = IconFragment()
//                iconFragment.setTargetFragment(this, TARGET_FRAGMENT_REQUEST_CODE)
//                var ft = requireActivity().supportFragmentManager.beginTransaction()
//                iconFragment.show(ft, "IconFragment")
            }
//            R.id.editCategoryLnk -> {
//                categoryViewModel.listAllCategories().observe(viewLifecycleOwner, Observer { categoriesList ->
//                    Log.d(TAG, "onCreateView: ListOfCategories: ${categoriesList.size}")
//                    adapter = CategoryAdapter(categoriesList, true, this)
//                    Log.d(TAG, "onCreateView: Adapter: ${adapter}")
//                    binding.categoryRecyclerView.adapter = adapter
//                })
//            }
        }
    }

    /**
     * Method used to initialize the Model for Database
     */
    private fun initializeModel() {
        //Create a DAO Instance
        val application = requireNotNull(this.activity).application

        //CategoryDAO
        val categoryDao: CategoryDAO = ExpenseTrackerDB.getInstance(application).categoryDao
        val categoryRepository: CategoryRepository = CategoryRepository(categoryDao)
        val categoryViewModelFactory: CategoryViewModelFactory = CategoryViewModelFactory(categoryRepository)
        categoryViewModel = ViewModelProviders.of(this, categoryViewModelFactory).get(CategoryViewModel::class.java)

        //ExpenseDAO
        val expenseDao: ExpenseDAO = ExpenseTrackerDB.getInstance(application).expenseDao
        val expenseRepository: ExpenseRepository = ExpenseRepository(expenseDao)
        val expenseViewModelFactory: ExpenseViewModelFactory = ExpenseViewModelFactory(expenseRepository)
        expenseViewModel = ViewModelProviders.of(this, expenseViewModelFactory).get(ExpenseViewModel::class.java)
    }

    override fun onListInteraction(category: CategoryEntity) {
        Log.d(TAG, "CategoryToBe Delete: ${category.categoryName}")
        //categoryViewModel.deleteCategory(category)
    }

    override fun onEditItemClickListner(categoryEntity: CategoryEntity) {
        var bundle = Bundle()
        var json = Gson()
        bundle.putString("CATEGORY_OBJ", json.toJson(categoryEntity))

        newCategoryFragment = NewCategoryFragment()
        newCategoryFragment.setTargetFragment(this, TARGET_FRAGMENT_REQUEST_CODE)
        newCategoryFragment.arguments = bundle
        var ft = requireActivity().supportFragmentManager.beginTransaction()
        newCategoryFragment.show(ft, "NewCaegoryFragment")
    }

    override fun onDeleteItemClickListner(categoryEntity: CategoryEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "CategoryUSED: ${checkCategoryUsedInExpense(categoryEntity)}")
            if(checkCategoryUsedInExpense(categoryEntity) == 0) {
                if (deleteCategory(categoryEntity) != -1) {
                    updateCategoryAdapter()
                    withContext(Dispatchers.Main) {
                        displayDialog("Message","Category Deleted.")
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    displayDialog("Alert","You can't delete Category. It is used in Expense.")
                }
            }
        }
    }

    /**
     * Method used to delete the category
     */
    suspend fun deleteCategory(categoryEntity: CategoryEntity): Int {
        return categoryViewModel.deleteCategory(categoryEntity)
    }

    /**
     * Method used to check if the Category id already used in Expense, if yes, you can't delete the category
     * else delete the category
     */
    suspend fun checkCategoryUsedInExpense(categoryEntity: CategoryEntity): Int {
        return expenseViewModel.checkForCategory(categoryEntity.caregoryId)
    }

    /**
     * Method used to update the adapter after deleteing a categoty
     */
     private fun updateCategoryAdapter() {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .detach(this)
                .attach(this)
                .commit()
    }

    /**
     * Method used to display Dialog
     */
    private fun displayDialog(title: String, message: String) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(resources.getString(R.string.dialogOk)) { dialog, id ->}
            .show()
    }

}
