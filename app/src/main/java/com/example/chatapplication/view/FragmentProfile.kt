package com.example.chatapplication.view

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.chatapplication.R
import com.example.chatapplication.model.User
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

class FragmentProfile: Fragment() {

    lateinit var profileImage: CircleImageView
    lateinit var fullName: TextView
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
    lateinit var imageUri: Uri

    private val PICK_IMAGE: Int = 2020
    private var filePath: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)

        profileImage = view.findViewById(R.id.profileImg)
        fullName = view.findViewById(R.id.fullName1)

        val backButton: FloatingActionButton = view.findViewById(R.id.close)
        val logout: Button = view.findViewById(R.id.logout1)

        storage = FirebaseStorage.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageReference = storage.getReference()


        val profileRef: StorageReference = storageReference.child(
            "users/" + (FirebaseAuth.getInstance()
                .currentUser?.uid) + "/profile.jpg")

        profileRef.downloadUrl.addOnSuccessListener{
            Picasso.get().load(it).into(profileImage)

        }

        databaseReference =
            FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser.uid)

        databaseReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                fullName.text = user!!.userName

                //profileImage.setImageResource(R.drawable.image2)

                /*if (user.profileImage == "") {
                    profileImage.setImageResource(R.drawable.image2)
                } else {
                    Picasso.get().load(user!!.profileImage).into(profileImage)
                    //Glide.with(applicationContext).load(user.profileImage).into(profileImage)
                }*/
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        profileImage.setOnClickListener {
            chooseImage()
        }

        //uploadImage()

        return view
    }

    private fun uploadImage() {
        if (filePath != null) {

            var ref: StorageReference = storageReference.child("image/" + UUID.randomUUID().toString())
            ref.putFile(filePath!!)
                .addOnSuccessListener {

                    val hashMap:HashMap<String,String> = HashMap()
                    hashMap["userName"] = fullName.text.toString()
                    hashMap["profileImage"] = filePath.toString()
                    databaseReference.updateChildren(hashMap as Map<String, Any>)
                    Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT)
                        .show()

                }
        }
    }

    private fun chooseImage() {
        val intent: Intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                imageUri = data?.getData()!!

                if (imageUri != null) {
                    uploadImageToFirebase(imageUri, profileImage)
                }
            }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri, profileImage: CircleImageView) {

        storageReference = storage.reference

        val fileRef: StorageReference = storageReference.child(
            "users/" + (FirebaseAuth.getInstance()
                .currentUser?.uid) + "/profile.jpg")

        fileRef.putFile(imageUri).addOnSuccessListener {
            Toast.makeText(context, "Image uploaded", Toast.LENGTH_SHORT).show()

            fileRef.downloadUrl.addOnSuccessListener {
                Picasso.get().load(imageUri).into(profileImage)
            }

            val hashMap:HashMap<String,String> = HashMap()
            hashMap["userName"] = fullName.text.toString()
            hashMap["profileImage"] = imageUri.toString()
            databaseReference.updateChildren(hashMap as Map<String, Any>)

        }.addOnFailureListener {
            Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
        }
    }
}
