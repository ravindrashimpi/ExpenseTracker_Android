package com.deere.exptracker.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.deere.exptracker.DAO.UserDAO
import com.deere.exptracker.R
import com.deere.exptracker.databinding.FragmentLoginBinding
import com.deere.exptracker.model.UserViewModel
import com.deere.exptracker.model.UserViewModelFactory
import com.deere.exptracker.repository.UserRepository
import com.deere.exptracker.signup.FakeUserViewModel
import com.deere.exptracker.signup.FakeUserViewModelFactory
import com.deere.exptracker.util.ExpenseTrackerDB
import com.deere.exptracker.util.SessionManagement

class LoginFragment : Fragment(), View.OnClickListener {

    lateinit var binding: FragmentLoginBinding
    lateinit var navController: NavController
    lateinit var fakeUserViewModelFactory: FakeUserViewModelFactory
    lateinit var fakeUserViewModel: FakeUserViewModel
    lateinit var sessionManagement: SessionManagement
    private lateinit var userViewModel: UserViewModel
    lateinit var selectedFragment: Fragment
    var TAG = "LoginFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView started")

        //Do the binding of the Fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        initializeViewModel()
        return binding.root
    }

    private fun initializeViewModel() {
        //Create a DAO Instance
        val application = requireNotNull(this.activity).application
        val userDao: UserDAO = ExpenseTrackerDB.getInstance(application).userDao
        val userRepository: UserRepository = UserRepository(userDao)
        val userViewModelFactory: UserViewModelFactory = UserViewModelFactory(userRepository)
        userViewModel =
            ViewModelProviders.of(this, userViewModelFactory).get(UserViewModel::class.java)

        //Initialize the sessionManagement Object
        sessionManagement = SessionManagement(application.applicationContext)
        Log.d(TAG, "Session Management Obj: ${sessionManagement}")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated started")

        //This will have reference to the Navigation Graph
        navController = Navigation.findNavController(view)

        //Set the onClick listner for the SignIn and SignUp button
        binding.SignIn.setOnClickListener(this)
        binding.registerLink.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.SignIn -> {
                var isValid: Boolean = true
                if (binding.emailid.text.toString().isEmpty()) {
                    //binding.passwordInputLayout.isErrorEnabled = false
                    //binding.emailIdInputLayout.error = "Email Id is mandatory."
                    isValid = false
                } else if (binding.password.text.toString().isEmpty()) {
                    // binding.emailIdInputLayout.isErrorEnabled = false
                    //binding.passwordInputLayout.error = "Password is mandatory."
                    isValid = false
                } else {
                    //binding.emailIdInputLayout.isErrorEnabled = false
                    //binding.passwordInputLayout.isErrorEnabled = false
                    isValid = true
                }

                if (isValid) {
                    Log.d(TAG, "EmailId: ${binding.emailid.text.toString()}")
                    Log.d(TAG, "Password: ${binding.password.text.toString()}")
                    //userViewModel.deleteAllUser()
                    userViewModel.validateUser(
                        binding.emailid.text.toString(),
                        binding.password.text.toString()
                    ).observe(viewLifecycleOwner, Observer { userEntity ->
                        Log.d(TAG, "IS User Valid: ${userEntity}")
                        if (userEntity != null) {
                            navController.navigate(R.id.action_loginFragment_to_dashboardFragment)
                            sessionManagement.saveSession(userEntity)
                        } else {
                            Toast.makeText(context, "Invalid EmailId/Password.", Toast.LENGTH_SHORT)
                                .show();
                        }
                    })
                }
            }
            R.id.registerLink -> navController.navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
