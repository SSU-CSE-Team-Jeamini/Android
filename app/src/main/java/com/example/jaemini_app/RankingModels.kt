package com.example.jaemini_app

import com.google.gson.annotations.SerializedName

/**
 * 랭킹 아이템
 */
data class RankingItem(
    val rank: Int,
    val name: String,
    val kcal: Int,
    @SerializedName("punch_count")
    val punchCount: Int,
    val username: String? = null
)

/**
 * 랭킹 응답 모델
 *
 * 서버 응답 형식:
 * {
 *   "status": "success",
 *   "data": {
 *     "my_rank": 4,
 *     "my_percent": 40,
 *     "my_kcal": 1200,
 *     "total_users": 100,
 *     "top3": [...],
 *     "list": [...]
 *   }
 * }
 */
data class RankingResponse(
    // 서버 응답 필드
    val status: String? = null,
    val message: String? = null,
    val data: RankingData? = null,

    // 하위 호환 (data 없이 직접 올 수도 있음)
    @SerializedName("my_rank")
    val my_rank: Int? = null,
    @SerializedName("my_percent")
    val my_percent: Int? = null,
    @SerializedName("my_kcal")
    val my_kcal: Int? = null,
    @SerializedName("total_users")
    val total_users: Int? = null,
    val top3: List<RankingItem>? = null,
    val list: List<RankingItem>? = null
) {
    /**
     * 실제 데이터를 반환하는 편의 메서드
     */
    fun getActualData(): RankingData {
        // data가 있으면 그대로 반환
        if (data != null) {
            return data
        }

        // data가 없으면 하위 호환 필드로 생성
        return RankingData(
            myRank = my_rank ?: 0,
            myPercent = my_percent ?: 0,
            myKcal = my_kcal ?: 0,
            totalUsers = total_users ?: 0,
            top3 = top3 ?: emptyList(),
            list = list ?: emptyList()
        )
    }
}

/**
 * 랭킹 데이터 (실제 데이터)
 */
data class RankingData(
    @SerializedName("my_rank")
    val myRank: Int,
    @SerializedName("my_percent")
    val myPercent: Int,
    @SerializedName("my_kcal")
    val myKcal: Int,
    @SerializedName("total_users")
    val totalUsers: Int,
    val top3: List<RankingItem>,
    val list: List<RankingItem>
)