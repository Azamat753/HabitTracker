package com.lawlett.habittracker.helper

interface TokenCallback {
    fun newToken(authCode:String)
    fun signSuccess()
}