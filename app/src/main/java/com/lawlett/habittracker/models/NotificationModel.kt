package com.lawlett.habittracker.models

data class NotificationModel(
    val message: MessageModel
)

data class MessageModel(
    var topic: String,
    var notification: NotificationMessage
)

data class FirebaseResponse(
    val name: String?
)

data class NotificationMessage(
    val title: String,
    val body: String
)

data class TokenModel(
    var access_token:String,
    var expires_in:Int,
    var scope:String,
    var token_type:String,
    var id_token:String,
)
