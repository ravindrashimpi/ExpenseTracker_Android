package com.deere.exptracker.expense

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.addCallback
import androidx.navigation.NavController
import androidx.navigation.Navigation

import com.deere.exptracker.R
import com.deere.exptracker.databinding.FragmentExpenseBinding
import com.deere.exptracker.util.Utils
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_expense.*
import java.text.SimpleDateFormat
import java.util.*

class ExpenseFragment : Fragment(), View.OnClickListener  {

    var TAG = "ExpenseFragment"
    lateinit var binding: FragmentExpenseBinding
    lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //Do the binding of the Fragment
        binding = FragmentExpenseBinding.inflate(inflater)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated started")

        //This will have reference to the Navigation Graph
        navController = Navigation.findNavController(view)

        //Handle BackButton event
        val backBtnEvent = requireActivity().onBackPressedDispatcher.addCallback(this) {
            Log.d(TAG, "onViewCreated BackButton initiated")
            //navController!!.navigate(R.id.action_expenseFragment_to_dashboardFragment)
        }

//        binding.expCancelBtn.setOnClickListener(this)
//        binding.expDoneBtn.setOnClickListener(this)
//        binding.expDate.setOnClickListener(this)
//        binding.expCategory.setOnClickListener(this)

    }

    override fun onClick(v: View) {
        when(v.id) {
            //R.id.expCancelBtn -> navController!!.navigate(R.id.action_expenseFragment_to_dashboardFragment)
            //R.id.expDoneBtn -> navController!!.navigate(R.id.action_expenseFragment_to_expenseList)
//            R.id.expDate -> {
//                Log.d(TAG, "Date Text Clicked: ${R.id.expDate}")
//                val dateFormat = SimpleDateFormat("dd MMM, YYYY", Locale.US)
//                //Calendar
//                val cal = Calendar.getInstance()
//                val year = cal.get(Calendar.YEAR)
//                val month = cal.get(Calendar.MONTH)
//                val day = cal.get(Calendar.DAY_OF_MONTH)
//
//                val datePicker = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener{v,mYear,mMonth,mDay ->
//                    val selectedDate = Calendar.getInstance()
//                    selectedDate.set(Calendar.YEAR, mYear)
//                    selectedDate.set(Calendar.MONTH, mMonth)
//                    selectedDate.set(Calendar.DAY_OF_MONTH, mDay)
//                    val date = dateFormat.format(selectedDate.time)
//                    //binding.expDate.setText(date)
//                }, year, month, day)
//                datePicker.show()
//            }
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
}
