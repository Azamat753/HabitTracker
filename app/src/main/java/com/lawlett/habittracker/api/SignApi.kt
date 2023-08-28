package com.lawlett.habittracker.api

import com.lawlett.habittracker.models.TokenModel
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.Flow

interface SignApi {

    @POST("token")
    suspend fun getToken(
        @Query("client_id") clientId: String = "547684386780-a4jo0jmhap40fu7jll20mo81i08fife9.apps.googleusercontent.com",
        @Query("client_secret") clientSecret: String = "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",
        @Query("code") code: String,
        @Query("grant_type") grantType: String = "authorization_code",
        @Query("redirect_uri") redirect: String = "https://habittracker-85f07.firebaseapp.com/__/auth/handler",
    ): TokenModel
}