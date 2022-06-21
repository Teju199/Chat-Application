package com.example.chatapplication.api

import com.example.chatapplication.model.NotificationData

data class PushNotification(
    var data: NotificationData,
    var to:String
)