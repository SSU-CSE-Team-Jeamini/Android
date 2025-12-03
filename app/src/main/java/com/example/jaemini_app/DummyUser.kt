package com.example.jaemini_app

data class DummyUser(
    val id: String,
    val pw: String,
    val nickname: String,
    val birth: String? = null,   // ✅ 추가
    val age: Int? = null,        // ✅ 추가
    val height: Float,           // ✅ Float로 변경 (nullable 제거)
    val weight: Float,           // ✅ Float로 변경 (Int → Float)
    val gender: String? = null,  // ✅ 추가
    val totalCalorie: Int,
    val totalPunch: Int,
    val totalDays: Int
)