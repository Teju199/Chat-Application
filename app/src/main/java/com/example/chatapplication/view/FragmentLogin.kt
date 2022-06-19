package com.example.chatapplication.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.chatapplication.R
import com.example.chatapplication.model.UserAuthService
import com.example.chatapplication.viewmodel.LoginViewModel
import com.example.chatapplication.viewmodel.LoginViewModelFactory
import com.example.chatapplication.viewmodel.SharedViewModel
import com.example.chatapplication.viewmodel.SharedViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FragmentLogin: Fragment() {

    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var sharedViewModel: SharedViewModel
    //lateinit var firebaseUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_login, container, false)

        val loginEmail: EditText = view.findViewById(R.id.loginEmail)
        val loginPassword: EditText = view.findViewById(R.id.loginPassword)
        val loginButton: Button = view.findViewById(R.id.loginBtn)
        val forgotPassword: TextView = view.findViewById(R.id.forgotPasswordBtn)
        val signUp: TextView = view.findViewById(R.id.signUpBtn)
        firebaseAuth = FirebaseAuth.getInstance()

        /*firebaseUser = firebaseAuth.currentUser!!

        if(firebaseUser != null){
            val intent: Intent = Intent(activity, ActivityHomePage::class.java)
            startActivity(intent)
            activity?.finish()
        }*/

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory(UserAuthService()))
            .get(LoginViewModel::class.java)

        sharedViewModel = ViewModelProvider(
            this,
            SharedViewModelFactory(UserAuthService())
        )[SharedViewModel::class.java]


        loginButton.setOnClickListener(View.OnClickListener {

            var email: String = loginEmail.text.toString().trim()
            var password: String = loginPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    getContext(), "All fields are required",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                loginViewModel.userLogin(email, password)
                loginViewModel.loginStatus.observe(viewLifecycleOwner, Observer {

                    if (it.status) {
                        checkEmailVerification()
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                        val intent: Intent = Intent(activity, ActivityHomePage::class.java)
                        startActivity(intent)
                        activity?.finish()

                    } else {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                        sharedViewModel.setGotoLoginPageStatus(true)
                    }
                })
            }
            })

        forgotPassword.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            if (transaction != null) {
                transaction.replace(R.id.fragmentContainer, FragmentForgotPassword())
                transaction.disallowAddToBackStack()
                transaction.commit()
            }
        }

        signUp.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            if (transaction != null) {
                transaction.replace(R.id.fragmentContainer, FragmentSignUp())
                transaction.disallowAddToBackStack()
                transaction.commit()
            }
        }

        return view
    }

    private fun checkEmailVerification() {

        var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

        if (firebaseUser != null) {
            if (firebaseUser.isEmailVerified) {
                Toast.makeText(
                    getContext(), "Logged In",
                    Toast.LENGTH_SHORT
                ).show()

                val intent: Intent = Intent(activity, ActivityHomePage::class.java)
                startActivity(intent)

            } else {
                Toast.makeText(
                    getContext(), "Email verification is pending",
                    Toast.LENGTH_SHORT
                ).show()
                firebaseAuth?.signOut()
            }
        }
    }
}