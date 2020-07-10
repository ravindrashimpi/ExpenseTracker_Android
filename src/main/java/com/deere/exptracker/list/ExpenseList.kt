package com.deere.exptracker.list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation

import com.deere.exptracker.R
import com.deere.exptracker.databinding.FragmentExpenseListBinding
import com.deere.exptracker.expense.ExpenseData

class ExpenseList : Fragment(), View.OnClickListener {
    val TAG = "ExpenseList"
    lateinit var binding: FragmentExpenseListBinding
    lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Get a reference to the binding object and inflate the fragment views.
        binding = FragmentExpenseListBinding.inflate(inflater, container, false)

        val adapter: ExpenseListAdapter = ExpenseListAdapter(ExpenseData.expenseList)

        binding.expenseRecyclerView.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated started")

        //This will have reference to the Navigation Graph
        navController = Navigation.findNavController(view)

        //Set the onClick listner for the SignIn and SignUp button



    }

    override fun onClick(v: View?) {
        Log.d(TAG, "onClick ${v!!.id}")
        when(v!!.id) {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }



}
