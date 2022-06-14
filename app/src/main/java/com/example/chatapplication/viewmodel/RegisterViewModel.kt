package com.example.chatapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapplication.model.AuthListener
import com.example.chatapplication.model.UserAuthService

class RegisterViewModel(private val userAuthService: UserAuthService): ViewModel() {
    private val _registerStatus = MutableLiveData<AuthListener>()
    val registerStatus = _registerStatus as LiveData<AuthListener>

    fun registerUser(email: String, password: String, fullName:String){
        userAuthService.registerUser(email, password, fullName){
            if(it.status){
                _registerStatus.value = it
            }
        }
    }
}