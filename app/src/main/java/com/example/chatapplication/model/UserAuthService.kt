package com.example.chatapplication.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UserAuthService {

    var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var fstore: FirebaseFirestore = FirebaseFirestore.getInstance()
    lateinit var firebaseUser: FirebaseUser
    lateinit var storageReference: StorageReference
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var userId: String


    fun userLogin(email: String, password: String, listener: (AuthListener) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful()) {
                    listener(AuthListener(true, "User logged in successfully."))

                } else {
                    listener(AuthListener(false, "User failed to login"))
                }
            }
    }

    fun registerUser(
        userName:String,email:String,password:String,
        listener: (AuthListener) -> Unit,
    ) {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful()) {
                    val user: FirebaseUser? = firebaseAuth.currentUser
                    val userId: String = user!!.uid
                    listener(AuthListener(true, "User Registered successfully."))

                    val documentReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
                        .child(userId)

                    val hashMap: MutableMap<String, Any> = HashMap()
                    hashMap["userId"] = userId
                    hashMap["email"] = email
                    hashMap["userName"] = userName
                    hashMap["profileImage"] = " "

                    if (userId != null) {
                        documentReference.setValue(hashMap).addOnSuccessListener {
                            (AuthListener(true, "User details recorded."))
                        }

                            .addOnFailureListener {
                                (AuthListener(false, "Failed to add details."))
                            }
                    }

                } else {
                    listener(AuthListener(false, "Registration failed."))
                }

            }
        }

    fun passwordReset(emailEntered: String){
        firebaseAuth.sendPasswordResetEmail(emailEntered).addOnCompleteListener { task ->

            if (task.isSuccessful()) {
                AuthListener(true, "Reset password link sent")
            } else {
                AuthListener(false, "Failed to send email")
            }
        }
    }
}