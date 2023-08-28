package com.lawlett.habittracker.api

import com.lawlett.habittracker.models.FirebaseResponse
import com.lawlett.habittracker.models.NotificationModel
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface FirebaseApi {

    @POST("v1/projects/habittracker-85f07/messages:send")
    suspend fun sendRemoteNotification(
        @Body notificationModel: NotificationModel,
        @Header("Authorization") token: String
    ): FirebaseResponse?

}