package com.example.chatapplication.view

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.R
import com.example.chatapplication.api.PushNotification
import com.example.chatapplication.api.RetrofitInstance
import com.example.chatapplication.model.Chat
import com.example.chatapplication.model.ChatAdapter
import com.example.chatapplication.model.NotificationData
import com.example.chatapplication.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Time
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class FragmentChat(val userId: String, val userName: String, val imageUser: CircleImageView) : Fragment() {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var reference: DatabaseReference
    private var chatList = ArrayList<Chat>()
    private lateinit var chatRecyclerView: RecyclerView
    var topic = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        val tvUserName: TextView = view.findViewById(R.id.tvUserName1)
        val imageBack: ImageView = view.findViewById(R.id.imgBack)
        val sendMessageBtn: ImageButton = view.findViewById(R.id.btnSendMessage)
        val etMessage: EditText = view.findViewById(R.id.etMessage1)
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView)

        chatRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)


        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("users")
            .child(firebaseUser!!.uid)

        reference.addValueEventListener(object: ValueEventListener{
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)

                tvUserName?.setText(userName)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

        sendMessageBtn.setOnClickListener {
            var message: String = etMessage.text.toString()

            if(message.isEmpty()){
                Toast.makeText(context, "Message is empty", Toast.LENGTH_SHORT).show()
                etMessage.setText("")
            }
            else{
                sendMessage(firebaseUser.uid, userId, message)
                etMessage.setText("")
                topic = "/topics/$userId"
                PushNotification(NotificationData( userName!!,message),
                    topic).also {
                    sendNotification(it)
                }

            }
        }

        readMessage(firebaseUser.uid, userId)

        imageBack.setOnClickListener {
            activity?.onBackPressed()
        }

        return view
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendMessage(senderId: String, receiverId: String, message: String){
        var reference: DatabaseReference = FirebaseDatabase.getInstance().reference
        val current = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val time = current.format(formatter)

        var hashMap: HashMap<String, String> = HashMap()
        hashMap["senderId"] = senderId
        hashMap["receiverId"] = receiverId
        hashMap["message"] = message
        hashMap["time"] = time

        reference.child("chat").push().setValue(hashMap)
    }

    private fun readMessage(senderId: String, receiverId: String): RecyclerView{
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("chat")

        databaseReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()

                for(dataSnapshot: DataSnapshot in snapshot.children){
                    val chat = dataSnapshot.getValue(Chat::class.java)

                    if(chat!!.senderId == senderId && chat!!.receiverId == receiverId ||
                        chat!!.senderId == receiverId && chat!!.receiverId == senderId
                    ){
                            chatList.add(chat)
                    }
                }

                val chatAdapter = ChatAdapter(context!!, chatList, userId)
                chatRecyclerView.adapter = chatAdapter
                chatAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return chatRecyclerView
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d("TAG", "Response: ${Gson().toJson(response)}")
            } else {
                Log.e("TAG", response.errorBody()!!.string())
            }
        } catch(e: Exception) {
            Log.e("TAG", e.toString())
        }
    }

}

