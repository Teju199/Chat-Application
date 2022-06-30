package com.example.chatapplication.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapplication.model.Adapter
import com.example.chatapplication.R
import com.example.chatapplication.api.FirebaseService
import com.example.chatapplication.model.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class ActivityHomePage: AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var userList: ArrayList<User>
    lateinit var menuview: Menu
    lateinit var user: User
    lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        userList = ArrayList<User>()
        recyclerView = findViewById(R.id.userRecyclerView)

        val searchView: SearchView = findViewById(R.id.search)
        searchView.onActionViewCollapsed()
        searchView.clearFocus()

        userId = FirebaseAuth.getInstance().currentUser?.uid.toString()


        var imageBack: ImageView = findViewById(R.id.imgBack)


        var toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolBar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeButtonEnabled(true)

        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        imageBack.setOnClickListener {
            onBackPressed()
        }

        getUserList()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("onQueryTextChange", "query: $newText")
                var userAdapter: Adapter = Adapter(userList, this@ActivityHomePage)
                userAdapter.filter.filter(newText!!)
                return true
            }
        })
    }

    private fun getUserList(){
        var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
        databaseReference.addValueEventListener(object: ValueEventListener{

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()

                for(dataSnapShot: DataSnapshot in snapshot.children){

                    user = dataSnapShot.getValue(User::class.java)!!

                    if(user!!.userId != userId){
                        userList.add(user)
                    }
                }

                val userAdapter = Adapter(userList, this@ActivityHomePage)
                recyclerView.adapter = userAdapter
                userAdapter.notifyDataSetChanged()
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menuview = menu!!
        menuInflater.inflate(R.menu.menuview, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            R.id.action_profile ->{
                supportFragmentManager.beginTransaction().add(
                    R.id.fragmentContainer1,
                    FragmentProfile()
                ).commit()
            }

            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                supportFragmentManager.beginTransaction().add(
                    R.id.fragmentContainer1,
                    FragmentLogin()
                ).commit()

            }

            R.id.action_group_chat -> {
                val intent = Intent(this, ActivityGroupChat::class.java)
                startActivity(intent)

            }
        }
        return super.onOptionsItemSelected(item)
    }

}