package com.deere.exptracker.category

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BlendMode
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.deere.exptracker.DAO.CategoryDAO
import com.deere.exptracker.R
import com.deere.exptracker.databinding.FragmentCategoryBinding
import com.deere.exptracker.databinding.FragmentNewCategoryBinding
import com.deere.exptracker.entity.CategoryEntity
import com.deere.exptracker.entity.UserEntity
import com.deere.exptracker.model.CategoryViewModel
import com.deere.exptracker.model.CategoryViewModelFactory
import com.deere.exptracker.repository.CategoryRepository
import com.deere.exptracker.util.ExpenseTrackerDB
import com.deere.exptracker.util.IconFragment
import com.deere.exptracker.util.SessionManagement
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_icon.view.*
import kotlinx.android.synthetic.main.list_expense.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewCategoryFragment : DialogFragment(), View.OnClickListener {

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
    var selectedCategory: CategoryEntity? = null
    var tempImageView: ImageView? = null
    var iconSeleted = ""
    lateinit var sessionManagement: SessionManagement
    lateinit var userEntity: UserEntity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = FragmentNewCategoryBinding.inflate(inflater, container, false)

        //Inisitalize Session Management Object
        val application = requireNotNull(this.activity).application
        sessionManagement = SessionManagement(application.applicationContext)
        userEntity = sessionManagement.getSession()!!

        //initialize Model and DB
        initializeModel()

        //Get data from Bundle
        var bundleObj = arguments
        var gson = Gson()
        if(bundleObj != null) {
            selectedCategory = gson.fromJson<CategoryEntity>((bundleObj.get("CATEGORY_OBJ") as String), CategoryEntity::class.java)
            binding.categoryName.setText(selectedCategory!!.categoryName.toString())
            changeImageColorOnSelection(getBindingImage(selectedCategory!!.categoryImg))
            iconSeleted = selectedCategory!!.categoryImg
            binding.addCategory.setText("UPDATE")
            binding.screenLabel.setText("Update Category")
            Log.d(TAG, "UPDATE CATEGORY: ${bundleObj}")
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //This will have reference to the Navigation Graph
        //navController = Navigation.findNavController(view)

        //Initialized Click Listner
        //binding.categoryIcon.setOnClickListener(this)
        binding.addCategory.setOnClickListener(this)
        binding.cancelBTN.setOnClickListener(this)

        //Bind onclick event for Images
        binding.travel.setOnClickListener(this)
        binding.cloths.setOnClickListener(this)
        binding.eatingOut.setOnClickListener(this)
        binding.entertainment.setOnClickListener(this)
        binding.fuel.setOnClickListener(this)
        binding.general.setOnClickListener(this)
        binding.gift.setOnClickListener(this)
        binding.holiday.setOnClickListener(this)
        binding.kids.setOnClickListener(this)
        binding.shopping.setOnClickListener(this)
        binding.sports.setOnClickListener(this)
        binding.bills.setOnClickListener(this)
        binding.drinks.setOnClickListener(this)
        binding.medical.setOnClickListener(this)
        binding.mobile.setOnClickListener(this)
        binding.phone.setOnClickListener(this)
        binding.tea.setOnClickListener(this)
        binding.television.setOnClickListener(this)
        binding.wifi.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
//           R.id.categoryIcon -> {
//               Log.d(TAG, "onClick: Category Icon Clicked")
//               iconFragment = IconFragment()
//               iconFragment.setTargetFragment(this, TARGET_FRAGMENT_REQUEST_CODE)
//               var ft = requireActivity().supportFragmentManager.beginTransaction()
//               iconFragment.show(ft, "IconFragment")
//           }
            R.id.addCategory -> {
                if(selectedCategory != null) {
                    //Edit Category
                    var editCategoryEntity = CategoryEntity(selectedCategory!!.caregoryId, binding.categoryName.text.toString(), iconSeleted, userEntity.userId)
                    CoroutineScope(Dispatchers.IO).launch {
                        var isUpdated = editCategory(editCategoryEntity)
                        if(isUpdated != -1) {
                            withContext(Dispatchers.Main) {
                                displayDialog("Message", "Category updated.")
                                dismiss()
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
                } else {
                    //Add Category
                    if (validateInputs(binding)) {
                        Log.d(TAG, "ADD CAREGORY NAME: ${binding.categoryName.text.toString()}")
                        Log.d(TAG, "ADD IMAGE: ${iconSeleted}")
                        var categoryEntity = CategoryEntity(0, binding.categoryName.text.toString(), iconSeleted, userEntity.userId)
                        CoroutineScope(Dispatchers.IO).launch {
                            var isAdded = addCategory(categoryEntity)
                            Log.d(TAG, "CATEGORY IS ADDED: ${isAdded}")
                            if (isAdded != -1L) {
                                withContext(Dispatchers.Main) {
                                    displayDialog("Message", "Category added.")
                                    dismiss()
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
                    }
                }
            }
            R.id.cancelBTN -> {
                dismiss()
            }
            R.id.travel -> {
                iconSeleted = "ic_travel"
                binding.travel.setColorFilter(Color.parseColor("#FE8558"))
                changeImageColorOnSelection(binding.travel)
            }
            R.id.cloths -> {
                iconSeleted = "ic_cloths"
                binding.cloths.setColorFilter(Color.parseColor("#FE8558"))
                changeImageColorOnSelection(binding.cloths)
            }
            R.id.eating_out -> {
                iconSeleted = "ic_eating_out"
                binding.eatingOut.setColorFilter(Color.parseColor("#FE8558"))
                changeImageColorOnSelection(binding.eatingOut)
            }
            R.id.entertainment -> {
                iconSeleted = "ic_entertainment"
                binding.entertainment.setColorFilter(Color.parseColor("#FE8558"))
                changeImageColorOnSelection(binding.entertainment)
            }
            R.id.fuel -> {
                iconSeleted = "ic_fuel"
                binding.fuel.setColorFilter(Color.parseColor("#FE8558"))
                changeImageColorOnSelection(binding.fuel)
            }
            R.id.general -> {
                iconSeleted = "ic_general"
                binding.general.setColorFilter(Color.parseColor("#FE8558"))
                changeImageColorOnSelection(binding.general)
            }
            R.id.gift -> {
                iconSeleted = "ic_gift"
                binding.gift.setColorFilter(Color.parseColor("#FE8558"))
                changeImageColorOnSelection(binding.gift)
            }
            R.id.holiday -> {
                iconSeleted = "ic_holiday"
                binding.holiday.setColorFilter(Color.parseColor("#FE8558"))
                changeImageColorOnSelection(binding.holiday)
            }
            R.id.kids -> {
                iconSeleted = "ic_kids"
                binding.kids.setColorFilter(Color.parseColor("#FE8558"))
                changeImageColorOnSelection(binding.kids)
            }
            R.id.shopping -> {
                iconSeleted = "ic_shopping"
                binding.shopping.setColorFilter(Color.parseColor("#FE8558"))
                changeImageColorOnSelection(binding.shopping)
            }
            R.id.sports -> {
                iconSeleted = "ic_sports"
                binding.sports.setColorFilter(Color.parseColor("#FE8558"))
                changeImageColorOnSelection(binding.sports)
            }
            R.id.bills -> {
                iconSeleted = "ic_bills"
                binding.bills.setColorFilter(Color.parseColor("#FE8558"))
                changeImageColorOnSelection(binding.bills)
            }
            R.id.drinks -> {
                iconSeleted = "ic_drinks"
                binding.drinks.setColorFilter(Color.parseColor("#FE8558"))
                changeImageColorOnSelection(binding.drinks)
            }
            R.id.mobile -> {
                iconSeleted = "ic_mobile"
                binding.mobile.setColorFilter(Color.parseColor("#FE8558"))
                changeImageColorOnSelection(binding.mobile)
            }
            R.id.phone -> {
                iconSeleted = "ic_phone"
                binding.phone.setColorFilter(Color.parseColor("#FE8558"))
                changeImageColorOnSelection(binding.phone)
            }
            R.id.tea -> {
                iconSeleted = "ic_tea"
                binding.tea.setColorFilter(Color.parseColor("#FE8558"))
                changeImageColorOnSelection(binding.tea)
            }
            R.id.medical -> {
                iconSeleted =  "ic_medical"
                binding.medical.setColorFilter(Color.parseColor("#FE8558"))
                changeImageColorOnSelection(binding.medical)
            }
            R.id.wifi -> {
                iconSeleted = "ic_wifi"
                binding.wifi.setColorFilter(Color.parseColor("#FE8558"))
                changeImageColorOnSelection(binding.wifi)
            }
            R.id.television -> {
                iconSeleted = "ic_television"
                binding.television.setColorFilter(Color.parseColor("#FE8558"))
                changeImageColorOnSelection(binding.television)
            }
        }
    }

    private fun changeImageColorOnSelection(img: ImageView) {
        if(tempImageView != null) {
            if (tempImageView!!.colorFilter.equals(img.colorFilter)) {
                img.setColorFilter(Color.parseColor("#FE8558"))
                tempImageView!!.setColorFilter(Color.parseColor("#605E5E"))
                tempImageView = img
            }
        } else {
            img.setColorFilter(Color.parseColor("#FE8558"))
            tempImageView = img
        }
    }

    /**
     * Method used to add the Category
     */
    suspend fun addCategory(categoryEntity: CategoryEntity): Long {
        return categoryViewModel.addCategory(categoryEntity)
    }

    /**
     * Method used to update the Category
     */
    suspend fun editCategory(categoryEntity: CategoryEntity): Int {
        return categoryViewModel.updateCategory(categoryEntity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: resultCode: ${resultCode} requestCode: ${requestCode}")
        if(resultCode != Activity.RESULT_OK) {
            return
        }

        if(requestCode == TARGET_FRAGMENT_REQUEST_CODE) {
            //binding.categoryIcon.setText(data!!.getStringExtra("imgName"))
            //binding.categoryIcon.setCompoundDrawablesWithIntrinsicBounds(changeIconSize(data), null, null, null)
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

    /**
     * Method used to validate the input fields
     */
    private fun validateInputs(binding: FragmentNewCategoryBinding): Boolean {
        return when {
            binding.categoryName.text.toString().isEmpty() -> {
                displayDialog("Warning","Please enter the Category Name.")
                false
            }
            iconSeleted.isEmpty() -> {
                displayDialog("Warning","Please select Icon for category.")
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
            .setPositiveButton(resources.getString(R.string.dialogOk)) { dialog, id ->}
            .show()
    }

    /**
     * Function used to get the binding image
     */
    private fun getBindingImage(imageName: String): ImageView {
        when(imageName) {
            "ic_travel" -> return binding.travel
            "ic_cloths" -> return binding.cloths
            "ic_eating_out" -> return binding.eatingOut
            "ic_entertainment" -> return binding.entertainment
            "ic_fuel" -> return binding.fuel
            "ic_general" -> return binding.general
            "ic_gift" -> return binding.gift
            "ic_holiday" -> return binding.holiday
            "ic_kids" -> return binding.kids
            "ic_shopping" -> return binding.shopping
            "ic_sports" -> return binding.sports
            "ic_bills" -> return binding.bills
            "ic_drinks" -> return binding.drinks
            "ic_mobile" -> return binding.mobile
            "ic_phone" -> return binding.phone
            "ic_tea" -> return binding.tea
            "ic_medical" -> return binding.medical
            "ic_wifi" -> return binding.wifi
            "ic_television" -> return binding.television
            else -> return binding.travel
        }
    }


}
