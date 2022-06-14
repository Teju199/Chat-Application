package com.example.chatapplication.view

import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.chatapplication.R
import com.example.chatapplication.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class FragmentChat(val userId: String, val userName: String, val imageUser: CircleImageView) : Fragment() {

    lateinit var firebaseUser: FirebaseUser
    lateinit var reference: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val tvUserName: TextView = view


            .findViewById(R.id.tvUserName1)
        val imgProfile: CircleImageView = view?.findViewById(R.id.imgProfile1)


        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("users")
            .child(firebaseUser!!.uid)

        reference.addValueEventListener(object: ValueEventListener{
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)

                tvUserName?.setText(userName)

                if (user?.profileImage == "") {
                    imgProfile?.setImageResource(R.drawable.profile_image)
                } else {
                    //Glide.with(context!!).load(user?.profileImage).into(imgProfile!!)
                }

                //Picasso.get().load(imageUser as Uri).into(imgProfile)


                /*val profileRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                    "users/$userId/profile.jpg")

                //Picasso.get().load(imageUser as String?).into(imgProfile)

                profileRef.downloadUrl.addOnSuccessListener {
                    Picasso.get().load(imageUser as String?).into(imgProfile)
                }*/

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })



        return inflater.inflate(R.layout.fragment_chat, container, false)
    }
}

