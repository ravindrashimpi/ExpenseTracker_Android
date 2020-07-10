package com.deere.exptracker.category

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.deere.exptracker.DAO.CategoryDAO
import com.deere.exptracker.R
import com.deere.exptracker.databinding.FragmentNewCategoryBinding
import com.deere.exptracker.entity.CategoryEntity
import com.deere.exptracker.model.CategoryViewModel
import com.deere.exptracker.model.CategoryViewModelFactory
import com.deere.exptracker.repository.CategoryRepository
import com.deere.exptracker.util.ExpenseTrackerDB
import com.deere.exptracker.util.IconFragment

class NewCategoryFragment : Fragment(), View.OnClickListener{

    //-----------New for Fragment-Dialog interaction
    val TARGET_FRAGMENT_REQUEST_CODE = 1
    lateinit var iconFragment: IconFragment
    //-----------------------------------

    val TAG = "NewCategoryFragment"
    lateinit var binding: FragmentNewCategoryBinding
    lateinit var navController: NavController
    lateinit var bundle: Bundle
    lateinit var listCategoryFragment: ListCategoryFragment
    lateinit var categoryViewModel: CategoryViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = FragmentNewCategoryBinding.inflate(inflater, container, false)

        //initialize Model and DB
        initializeModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //This will have reference to the Navigation Graph
        navController = Navigation.findNavController(view)

        //Initialized Click Listner
        binding.categoryIcon.setOnClickListener(this)
        binding.addCategory.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v!!.id) {
           R.id.categoryIcon -> {
               Log.d(TAG, "onClick: Category Icon Clicked")
               iconFragment = IconFragment()
               iconFragment.setTargetFragment(this, TARGET_FRAGMENT_REQUEST_CODE)
               var ft = requireActivity().supportFragmentManager.beginTransaction()
               iconFragment.show(ft, "IconFragment")
           }
           R.id.addCategory -> {
               var categoryEntity = CategoryEntity(0,
                                                    binding.categoryName.text.toString(),
                                                    binding.categoryIcon.text.toString())
               categoryViewModel.addCategory(categoryEntity)

               listCategoryFragment = ListCategoryFragment()
               requireActivity().supportFragmentManager
                   .beginTransaction()
                   .add(R.id.fragment_container, listCategoryFragment)
                   .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                   .addToBackStack(null)
                   .commit()
           }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: resultCode: ${resultCode} requestCode: ${requestCode}")
        if(resultCode != Activity.RESULT_OK) {
            return
        }

        if(requestCode == TARGET_FRAGMENT_REQUEST_CODE) {
            binding.categoryIcon.setText(data!!.getStringExtra("imgName"))
            binding.categoryIcon.setCompoundDrawablesWithIntrinsicBounds(changeIconSize(data), null, null, null)
        }
    }

    /**
     * Below method will be used under IconFragmentDialog to send data back to this fragment
     */
    public fun newIntent(imgSrc: Int, imgName: String) : Intent {
        var intent = Intent()
        intent.putExtra ("imgSrc", imgSrc)
        intent.putExtra("imgName", imgName)
        return intent
    }

    /**
     * Method used to change the size of icon
     */
    private fun changeIconSize(data: Intent): Drawable {
        var icon: Drawable = resources.getDrawable(data!!.getIntExtra("imgSrc",0))
        var bitMap = (icon as BitmapDrawable).bitmap
        var d = BitmapDrawable(resources, Bitmap.createScaledBitmap(bitMap, 80, 80, true)) as Drawable
        return d
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

}
