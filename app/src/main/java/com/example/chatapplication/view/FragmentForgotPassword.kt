package com.example.chatapplication.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.chatapplication.R
import com.example.chatapplication.model.UserAuthService
import com.google.firebase.auth.FirebaseAuth

class FragmentForgotPassword : Fragment() {

    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_forgotpassword, container, false)

        val emailToSendRecoverPassword: EditText = view.findViewById(R.id.emailForPasswordRecover)
        val recoverPassword: Button = view.findViewById(R.id.passwordrecoverbtn)
        val backToLoginPage: TextView = view.findViewById(R.id.backToLogin2)

        firebaseAuth = FirebaseAuth.getInstance()


        recoverPassword.setOnClickListener {
            val email: String = emailToSendRecoverPassword.text.toString()
            if (email.isEmpty()) {
                Toast.makeText(
                    getContext(), "Enter valid email id",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                val userAuthService: UserAuthService = UserAuthService()

                userAuthService.passwordReset(email)
                Toast.makeText(context, "Reset password link sent", Toast.LENGTH_SHORT).show()

                backToLoginPage()
            }
        }

        backToLoginPage.setOnClickListener {
            backToLoginPage()
        }

        return view
    }

    fun backToLoginPage() {

        val transaction = activity?.supportFragmentManager?.beginTransaction()
        if (transaction != null) {
            transaction.replace(R.id.fragmentContainer, FragmentLogin())
            transaction.disallowAddToBackStack()
            transaction.commit()
        }
    }
}