package com.example.jaemini_app

data class ProfileResponse(
    val user_id: Int,           // 추가
    val username: String,       // ✅ 이미 있음
    val name: String,           // nickname → name으로 변경
    val age: Int,               // 추가
    val height: Float,          // 추가
    val weight: Float,          // 추가
    val gender: String,         // 추가
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