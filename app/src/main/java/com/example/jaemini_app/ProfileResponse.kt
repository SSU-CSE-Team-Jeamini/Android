package com.example.jaemini_app

data class ProfileResponse(
    val username: String,
    val totalDays: Int,
    val totalKcal: Int,
    val totalPunch: Int,
    val achievements: List<Achievement>? = null
)

data class Achievement(
    val id: Int,
    val name: String,
    val isCompleted: Boolean
)