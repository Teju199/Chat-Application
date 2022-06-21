package com.example.chatapplication.model

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivities
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.R
import com.example.chatapplication.view.FragmentChat
import com.example.chatapplication.view.FragmentLogin
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text
import java.util.*
import javax.annotation.meta.When
import kotlin.collections.ArrayList


class Adapter(private val userList: ArrayList<User>, private val context: Context):
    RecyclerView.Adapter<Adapter.MyViewHolder>(), Filterable {
    private lateinit var lastMessage: String
    private lateinit var time: String

    var userFilterList = ArrayList<User>()

    init {
        userFilterList = this.userList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.users_cardview, parent, false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = userList[position]
        holder.txtUserName.text = user.userName

        lastMessage(user.userId, holder.txtTemp, holder.tempTime)


        val profileRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            "users/" + (user.userId) + "/profile.jpg")

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
        val tempTime: TextView = itemView.findViewById(R.id.tempTime)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                if (constraint!!.isEmpty()) {
                    userFilterList = userList
                } else {
                    val resultList = ArrayList<User>()
                    for (user in userList) {
                        if (user.userName.lowercase(Locale.getDefault())
                                .contains(constraint.toString())
                        ) {
                            resultList.add(user)
                        }
                        userFilterList = resultList
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = userFilterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                userFilterList = results?.values as java.util.ArrayList<User>
                notifyDataSetChanged()
            }
        }
    }

    fun lastMessage(userid: String, tempText: TextView, tempTime: TextView) {
        lastMessage = "default"
        time = "default"
        var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        var ref: DatabaseReference = FirebaseDatabase.getInstance().getReference("chat")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    var chat: Chat? = dataSnapShot.getValue(Chat::class.java)

                    if (chat!!.receiverId.equals(firebaseUser?.uid) && chat!!.senderId.equals(userid) ||
                        chat!!.receiverId.equals(userid) && chat!!.senderId.equals(firebaseUser?.uid)
                    ) {
                        lastMessage = chat.message
                        time = chat.time
                    }
                }

                when(lastMessage){
                    "default" -> {
                        tempText.text = "No message"
                    }

                    else -> {
                        tempText.text = lastMessage
                        tempTime.text = time
                    }
                }

                lastMessage = "default"
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}