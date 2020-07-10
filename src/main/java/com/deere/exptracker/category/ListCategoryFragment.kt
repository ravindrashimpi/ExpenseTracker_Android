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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deere.exptracker.DAO.CategoryDAO
import com.deere.exptracker.R
import com.deere.exptracker.databinding.FragmentCategoryBinding
import com.deere.exptracker.entity.CategoryEntity
import com.deere.exptracker.model.CategoryViewModel
import com.deere.exptracker.model.CategoryViewModelFactory
import com.deere.exptracker.repository.CategoryRepository
import com.deere.exptracker.util.ExpenseTrackerDB


class ListCategoryFragment : Fragment(), View.OnClickListener, CategoryAdapter.OnListInteractionListener {
    var TAG = "ListCategoryFragment"
    lateinit var navController: NavController
    lateinit var binding: FragmentCategoryBinding
    lateinit var newCategoryFragment: NewCategoryFragment
    lateinit var categoryViewModel: CategoryViewModel
    lateinit var adapter: CategoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView started")
        // Get a reference to the binding object and inflate the fragment views.
        binding = FragmentCategoryBinding.inflate(inflater, container, false)

        //initialize Model and DB
        initializeModel()
        categoryViewModel.listAllCategories().observe(viewLifecycleOwner, Observer { categoriesList ->
            Log.d(TAG, "onCreateView: ListOfCategories: ${categoriesList.size}")
            adapter = CategoryAdapter(categoriesList, false, this)
            Log.d(TAG, "onCreateView: Adapter: ${adapter}")
            binding.categoryRecyclerView.adapter = adapter
        })

        //App/Back Button


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //This will have reference to the Navigation Graph
        navController = Navigation.findNavController(view)

        //Initialized Click Listner
        binding.addCategoryImg.setOnClickListener(this)
        binding.editCategoryLnk.setOnClickListener(this)
    }

    @SuppressLint("ResourceType")
    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.addCategoryImg -> {
                newCategoryFragment = NewCategoryFragment()
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container, newCategoryFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commit()
            }
            R.id.editCategoryLnk -> {
                categoryViewModel.listAllCategories().observe(viewLifecycleOwner, Observer { categoriesList ->
                    Log.d(TAG, "onCreateView: ListOfCategories: ${categoriesList.size}")
                    adapter = CategoryAdapter(categoriesList, true, this)
                    Log.d(TAG, "onCreateView: Adapter: ${adapter}")
                    binding.categoryRecyclerView.adapter = adapter
                })
            }
        }
    }

    /**
     * Method will get called from Adapter to delete the category
     */
    fun deleteCategory(category: CategoryEntity) {
        categoryViewModel.deleteCategory(category)
    }

    /**
     * Method used to initialize the Model for Database
     */
    private fun initializeModel() {
        //Create a DAO Instance
        val application = requireNotNull(this.activity).application
        val categoryDao: CategoryDAO = ExpenseTrackerDB.getInstance(application).categoryDao
        val categoryRepository: CategoryRepository = CategoryRepository(categoryDao)
        val categoryViewModelFactory: CategoryViewModelFactory = CategoryViewModelFactory(categoryRepository)
        categoryViewModel = ViewModelProviders.of(this, categoryViewModelFactory).get(CategoryViewModel::class.java)
    }

    override fun onListInteraction(category: CategoryEntity) {
        Log.d(TAG, "CategoryToBe Delete: ${category.categoryName}")
        categoryViewModel.deleteCategory(category)
    }

}
