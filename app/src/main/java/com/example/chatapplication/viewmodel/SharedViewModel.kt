package com.example.chatapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapplication.model.UserAuthService
import com.google.firebase.firestore.FirebaseFirestore

class SharedViewModel(userAuthService: UserAuthService): ViewModel() {

    val fstore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _gotoLoginPageStatus = MutableLiveData<Boolean>()
    val gotoLoginPageStatus: LiveData<Boolean> = _gotoLoginPageStatus

    fun setGotoLoginPageStatus(status: Boolean){
        _gotoLoginPageStatus.value = status
    }

}