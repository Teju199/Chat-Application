package com.example.chatapplication.view

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapplication.databinding.ActivityGroupChatBinding
import com.example.chatapplication.model.Chat
import com.example.chatapplication.model.ChatAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "GroupChatActivity"
class ActivityGroupChat : AppCompatActivity() {

    private lateinit var binding: ActivityGroupChatBinding
    private lateinit var  db: FirebaseDatabase
    private lateinit var auth : FirebaseAuth
    private lateinit var chatAdapter: ChatAdapter
    private var chatList = ArrayList<Chat>()
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        binding.imgBack.setOnClickListener {
            val intentToMainActivity = Intent(this, MainActivity::class.java)
            startActivity(intentToMainActivity)
            finish()
        }

        binding.tvUserName1.text = "Group Chat"

        db.reference.child("Group chat").addValueEventListener(object: ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for(data in snapshot.children){
                    val message = data.getValue(Chat::class.java)

                    if (message != null) {
                        chatList.add(message)
                    }

                    chatAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        chatAdapter = ChatAdapter(this, chatList,null)
        binding.chatRecyclerView.adapter = chatAdapter
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.btnSendMessage.setOnClickListener {
            val messageBody = binding.etMessage1.text.toString()
            val senderID = auth.uid
            val currentDate = Date()
            val time: String = DateFormat.format("h:mm a", currentDate.time).toString()
            val chatObject = Chat(senderID!!,null, messageBody, time)

            sendMessageToFireStore(chatObject)
            binding.etMessage1.setText("")
        }
    }

    private fun sendMessageToFireStore( chat: Chat?) {
        db.reference.child("Group chat")
            .push()
            .setValue(chat)
            .addOnSuccessListener {
                db.reference.child("chat")
                    .push()
                    .setValue(chat)
                    .addOnSuccessListener {
                        Log.d(TAG, "Message added")
                    }
            }
    }
}