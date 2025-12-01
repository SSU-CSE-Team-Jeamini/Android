package com.example.jaemini_app

data class HomeResponse(
    val todayCalorie: Int,
    val todayPunch: Int,
    val weight: Float
)

data class GraphResponse(
    val type: String,  // "calorie", "punch", "rank"
    val data: List<GraphData>
)

data class GraphData(
    val date: String,  // "2024-01-01"
    val value: Float   // 칼로리, 펀치 횟수, 또는 순위 퍼센트
)