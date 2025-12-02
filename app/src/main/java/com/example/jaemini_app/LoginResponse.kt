package com.example.jaemini_app

data class LoginResponse(
    val status: String,
    val user_id: Int?,
    val token: String? = null
)
