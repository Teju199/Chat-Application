package com.example.chatapplication.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.chatapplication.R
import de.hdodenhof.circleimageview.CircleImageView

class FragmentGroup: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_groupchat, container, false)

        var image: CircleImageView = view.findViewById(R.id.groupImage)
        var title: EditText = view.findViewById(R.id.groupTitle)
        var description : EditText = view.findViewById(R.id.groupDescription)
        var backArrow: ImageView = view.findViewById(R.id.backBtn)

        backArrow.setOnClickListener {
            val intent: Intent = Intent(context, ActivityHomePage::class.java)
            startActivity(intent)
        }


        return view
    }
}