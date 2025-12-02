package com.example.jaemini_app

data class DummyUser(
    val id: String,
    val pw: String,
    val nickname: String,
    val weight: Int,
    val totalCalorie: Int,
    val totalPunch: Int,
    val totalDays: Int  // 총 운동일 추가
)