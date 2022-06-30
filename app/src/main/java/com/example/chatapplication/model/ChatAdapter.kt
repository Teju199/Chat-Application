package com.example.chatapplication.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class ChatAdapter(private val context: Context, private val chatList: ArrayList<Chat>, val userId: String?)
    : RecyclerView.Adapter<ChatAdapter.ViewHolder>(){

    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1
    var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        if (viewType == MESSAGE_TYPE_RIGHT) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_right, parent, false)
            return ViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_left, parent, false)
            return ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chatList[position]
        holder.txtUserNameChat.text = chat.message
        holder.timing.text = chat.time

        firebaseUser = FirebaseAuth.getInstance().currentUser

        if(chat.senderId == firebaseUser!!.uid){

            val profileRef: StorageReference = FirebaseStorage.getInstance().reference
                .child("users/${firebaseUser!!.uid}/profile.jpg")

            profileRef.downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).into(holder.imgUserChat)
            }
        }
        else{
            //val userId: String = FirebaseAuth.getInstance().uid.toString()

            val profileRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "users/${userId}/profile.jpg")

            profileRef.downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).into(holder.imgUserChat)
            }
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtUserNameChat: TextView = itemView.findViewById(R.id.tvMessage)
        val imgUserChat: CircleImageView = itemView.findViewById(R.id.userImage)
        val timing: TextView = itemView.findViewById(R.id.time)
    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if (chatList[position].senderId == firebaseUser!!.uid) {
            return MESSAGE_TYPE_RIGHT
        } else {
            return MESSAGE_TYPE_LEFT
        }

    }

}