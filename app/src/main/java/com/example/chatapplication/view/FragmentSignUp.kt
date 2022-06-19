package com.example.chatapplication.view

import android.net.Uri
import android.os.Bundle
import android.util.Patterns
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
import com.example.chatapplication.viewmodel.RegisterViewModel
import com.example.chatapplication.viewmodel.RegisterViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.makeramen.roundedimageview.RoundedImageView
import java.util.regex.Pattern
import kotlin.properties.Delegates

class FragmentSignUp: Fragment() {
    lateinit var signupEmail: EditText
    lateinit var signupPassword: EditText
    lateinit var fullName: EditText
    lateinit var confirmPassword: EditText
    lateinit var firebaseAuth: FirebaseAuth
    var PICK_IMAGE by Delegates.notNull<Int>()
    lateinit var storageReference: StorageReference
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var profileImage: RoundedImageView
    lateinit var imageUri: Uri
    lateinit var userID: String
    lateinit var fstore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_signup, container, false)

        fullName = view.findViewById(R.id.name)
        signupEmail = view.findViewById(R.id.signupEmail)
        signupPassword = view.findViewById(R.id.signupPassword)
        confirmPassword = view.findViewById(R.id.confirmPassword)
        val signupButton: Button = view.findViewById(R.id.signupBtn1)
        val backToLogin: TextView = view.findViewById(R.id.backToSignIn)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.getReference()
        fstore = FirebaseFirestore.getInstance()
        userID = firebaseAuth.currentUser?.uid.toString()
        PICK_IMAGE = 1


        signupButton.setOnClickListener(View.OnClickListener {
            val userName: String = fullName.getText().toString().trim()
            val email: String = signupEmail.getText().toString().trim()
            val password: String = signupPassword.getText().toString().trim()
            val confirmedPassword: String = confirmPassword.getText().toString().trim()

            signupProcess(userName, email, password, confirmedPassword)
        })

        backToLogin.setOnClickListener{
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            if (transaction != null) {
                transaction.replace(R.id.fragmentContainer, FragmentLogin())
                transaction.disallowAddToBackStack()
                transaction.commit()
            }
        }

        return view
    }

    private fun isValidPassword(password: String?): Boolean {
        val passwordPattern = "(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$%^&*-]).{8,}\$"
        val pattern = Pattern.compile(passwordPattern)
        val matcher = pattern.matcher(password)
        return matcher.matches()
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val pattern = Pattern.compile(emailPattern)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun signupProcess(userName: String, email: String, password: String, confirmedPassword:String) {
        val RegisterViewModel = ViewModelProvider(this, RegisterViewModelFactory(UserAuthService()))
            .get(RegisterViewModel::class.java)

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                context, "All fields are required",
                Toast.LENGTH_SHORT
            ).show()
        } else if (password.length < 7) {
            signupPassword.error = "Password length is small"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signupEmail.error = "Invalid email."
        } else if (!isValidPassword(password)) {
            signupPassword.error = "Invalid password."
        } else if (!isValidEmail(email)) {
            signupEmail.error = "Invalid email."
        } else if (password != confirmedPassword) {
            Toast.makeText(
                context, "Password do not match",
                Toast.LENGTH_SHORT
            ).show()
        }else {

            RegisterViewModel.registerUser(userName, email, password)
            RegisterViewModel.registerStatus.observe(viewLifecycleOwner, Observer {

                if (it.status) {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()

                    val firebaseUser: FirebaseUser? = firebaseAuth.currentUser

                    firebaseUser?.sendEmailVerification()?.addOnCompleteListener {
                        Toast.makeText(
                            getContext(), "Verification email sent",
                            Toast.LENGTH_SHORT
                        ).show()
                        firebaseAuth?.signOut()

                    }

                } else {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}


