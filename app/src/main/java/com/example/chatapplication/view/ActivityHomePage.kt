package com.example.chatapplication.view

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapplication.model.Adapter
import com.example.chatapplication.R
import com.example.chatapplication.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class ActivityHomePage: AppCompatActivity()  {

    lateinit var recyclerView: RecyclerView
    lateinit var userList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        val user = User()
        userList = ArrayList<User>()
        recyclerView = findViewById(R.id.userRecyclerView)

        var imageBack: ImageView = findViewById(R.id.imgBack)
        var profileImage : ImageView = findViewById(R.id.imgProfile1)

        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        imageBack.setOnClickListener {
            onBackPressed()
        }

        getUserList()

        profileImage.setOnClickListener{
            Toast.makeText(this, "clicked on profile", Toast.LENGTH_SHORT).show()

            supportFragmentManager.beginTransaction().add(
                R.id.fragmentContainer1,
                FragmentProfile()
            ).commit()

            Glide.with(this).load(user.profileImage).into(profileImage)
        }

    }

    private fun getUserList(){
        var firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
        var userId = firebase.uid
        databaseReference.addValueEventListener(object: ValueEventListener{

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()

                for(dataSnapShot: DataSnapshot in snapshot.children){

                    val user = dataSnapShot.getValue(User::class.java)

                    if(user!!.userId != firebase.uid){
                        userList.add(user)
                    }
                }

                val userAdapter = Adapter(userList, this@ActivityHomePage)
                recyclerView.adapter = userAdapter
            }

        })
    }

}