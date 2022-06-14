package com.example.chatapplication.model

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivities
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.R
import com.example.chatapplication.view.FragmentChat
import com.example.chatapplication.view.FragmentLogin
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class Adapter(private val userList: ArrayList<User>, private val context: Context):
    RecyclerView.Adapter<Adapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.users_cardview, parent, false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = userList[position]
        holder.txtUserName.text = user.userName

        val profileRef: StorageReference = FirebaseStorage.getInstance().getReference().child(
            "users/" + (user.userId
                    ) + "/profile.jpg"
        )

        profileRef.downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).into(holder.imageUser)
        }

        holder.layoutUser.setOnClickListener {

            Toast.makeText(context, "view clicked", Toast.LENGTH_SHORT).show()

            var activity: AppCompatActivity = it.context as AppCompatActivity

            activity.supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainer1,
                    FragmentChat(user.userId, user.userName, holder.imageUser)
                ).addToBackStack(null).commit()

        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtUserName: TextView = itemView.findViewById(R.id.userName)
        val txtTemp: TextView = itemView.findViewById(R.id.temp)
        val imageUser: CircleImageView = itemView.findViewById(R.id.userImage)
        val layoutUser: LinearLayout = itemView.findViewById(R.id.layoutUser)
    }
}