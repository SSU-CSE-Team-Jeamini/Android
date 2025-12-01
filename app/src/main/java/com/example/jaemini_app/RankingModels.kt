package com.example.jaemini_app

data class RankingItem(
    val rank: Int,
    val name: String,
    val kcal: Int,
    val punchCount: Int
)

data class RankingResponse(
    val my_rank: Int,
    val my_percent: Int,
    val top3: List<RankingItem>,
    val list: List<RankingItem>
)
