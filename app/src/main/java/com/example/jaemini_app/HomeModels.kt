package com.example.jaemini_app

import com.google.gson.annotations.SerializedName

/**
 * 홈 화면 응답 모델
 */
data class HomeResponse(
    val status: String? = null,
    val message: String? = null,
    val data: HomeData? = null,

    // 하위 호환
    @SerializedName("today_calorie")
    val todayCalorie: Int? = null,
    @SerializedName("today_punch")
    val todayPunch: Int? = null,
    val weight: Float? = null
) {
    fun getActualData(): HomeData {
        return data ?: HomeData(
            todayCalorie = todayCalorie ?: 0,
            todayPunch = todayPunch ?: 0,
            weight = weight ?: 70.0f
        )
    }
}

data class HomeData(
    @SerializedName("today_calorie")
    val todayCalorie: Int,
    @SerializedName("today_punch")
    val todayPunch: Int,
    val weight: Float
)

/**
 * 그래프 응답 모델
 */
data class GraphResponse(
    val status: String? = null,
    val message: String? = null,
    val data: GraphDataWrapper? = null,

    // 하위 호환
    val type: String? = null,
    @SerializedName("graph_data")
    val graphData: List<GraphData>? = null
) {
    fun getActualGraphData(): List<GraphData> {
        return data?.graphData ?: graphData ?: emptyList()
    }
}

data class GraphDataWrapper(
    val type: String,
    val period: String? = null,
    @SerializedName("graph_data")
    val graphData: List<GraphData>
)

data class GraphData(
    val date: String,
    val value: Float
)

/**
 * 랭크 데이터 응답 모델
 */
data class RankDataResponse(
    val status: String? = null,
    val message: String? = null,
    val data: RankData? = null,

    // 하위 호환
    @SerializedName("my_rank")
    val my_rank: Int? = null,
    @SerializedName("top_percent")
    val top_percent: Float? = null,
    @SerializedName("total_users")
    val total_users: Int? = null,
    @SerializedName("my_calorie")
    val my_calorie: Int? = null,
    val level: String? = null
) {
    fun getActualData(): RankData {
        return data ?: RankData(
            myRank = my_rank ?: 0,
            topPercent = top_percent ?: 0f,
            totalUsers = total_users ?: 0,
            myCalorie = my_calorie ?: 0,
            level = level
        )
    }
}

data class RankData(
    @SerializedName("my_rank")
    val myRank: Int,
    @SerializedName("top_percent")
    val topPercent: Float,
    @SerializedName("total_users")
    val totalUsers: Int,
    @SerializedName("my_calorie")
    val myCalorie: Int,
    val level: String? = null
)