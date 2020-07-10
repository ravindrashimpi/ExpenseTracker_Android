package com.deere.exptracker.signup

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.deere.exptracker.DAO.UserDAO
import com.deere.exptracker.R
import com.deere.exptracker.databinding.FragmentSignUpBinding
import com.deere.exptracker.entity.UserEntity
import com.deere.exptracker.model.UserViewModel
import com.deere.exptracker.model.UserViewModelFactory
import com.deere.exptracker.repository.UserRepository

import com.deere.exptracker.util.ExpenseTrackerDB
import com.deere.exptracker.util.InjectorUtil

class SignUpFragment : Fragment(), View.OnClickListener, View.OnFocusChangeListener {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var navController: NavController
    private lateinit var userViewModel: UserViewModel

    private lateinit var user: UserEntity
    var TAG = "SignUpFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //Do the binding of the Fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)

        initializeViewModel()
        return binding.root
    }

    private fun initializeViewModel() {
        //Create a DAO Instance
        val application = requireNotNull(this.activity).application
        val userDao: UserDAO = ExpenseTrackerDB.getInstance(application).userDao
        val userRepository: UserRepository = UserRepository(userDao)
        val userViewModelFactory: UserViewModelFactory = UserViewModelFactory(userRepository)
        userViewModel = ViewModelProviders.of(this, userViewModelFactory).get(UserViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated started")

        //This will have reference to the Navigation Graph
        navController = Navigation.findNavController(view)

        //Set the onClick listner for the SignIn and SignUp button
        binding.registerBTN.setOnClickListener(this)
        binding.firstName.setOnFocusChangeListener(this)
        binding.lastName.setOnFocusChangeListener(this)
        binding.emailid.setOnFocusChangeListener(this)
        binding.password.setOnFocusChangeListener(this)
        binding.confirmPassword.setOnFocusChangeListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.registerBTN -> {
                var isValid: Boolean = true
                if (binding.firstName.text.toString().isEmpty()) {
                    binding.firstNameInputLayout.error = "Please enter First Name."
                    isValid = false
                } else if (binding.lastName.text.toString().isEmpty()) {
                    binding.lastNameInputLayout.error = "Please enter Last Name."
                    isValid = false
                } else if (binding.emailid.text.toString().isEmpty()) {
                    binding.regEmailIdInputLayout.error = "Please enter Email-Id."
                    isValid = false
                } else if (binding.password.text.toString().isEmpty()) {
                    binding.regPasswordInputLayout.error = "Please enter Password."
                    isValid = false
                } else if (binding.confirmPassword.text.toString().isEmpty()) {
                    binding.confirmPasswordInputLayout.error = "Please enter Confirm Password."
                    isValid = false
                } else {
                    isValid = true
                }

                if(isValid) {
                    val userEntity = UserEntity(
                        0,
                        binding.firstName.text.toString(),
                        binding.lastName.text.toString(),
                        binding.emailid.text.toString(),
                        binding.password.text.toString()
                    )
                    userViewModel.registerUser(userEntity)
                    Toast.makeText(context, "Succesful Registration", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when(v!!.id) {
            R.id.firstName -> { if(!hasFocus) binding.firstNameInputLayout.error = "" }
            R.id.lastName -> { if(!hasFocus) binding.lastNameInputLayout.error = "" }
            R.id.emailid -> { if(!hasFocus) binding.regEmailIdInputLayout.error = "" }
            R.id.password -> { if(!hasFocus) binding.regPasswordInputLayout.error = "" }
            R.id.confirmPassword -> { if(!hasFocus) binding.confirmPasswordInputLayout.error = "" }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }



}
