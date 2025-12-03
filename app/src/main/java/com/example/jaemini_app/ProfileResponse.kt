package com.example.jaemini_app

import com.google.gson.annotations.SerializedName

/**
 * 프로필 응답 모델
 */
data class ProfileResponse(
    val status: String? = null,
    val message: String? = null,
    val data: ProfileData? = null,

    // 하위 호환 (data 없이 직접 올 수도 있음)
    @SerializedName("user_id")
    val user_id: Int? = null,
    val username: String? = null,
    val name: String? = null,
    val age: Int? = null,
    val height: Float? = null,
    val weight: Float? = null,
    val gender: String? = null,
    @SerializedName("total_days")
    val totalDays: Int? = null,
    @SerializedName("total_kcal")
    val totalKcal: Int? = null,
    @SerializedName("total_punch")
    val totalPunch: Int? = null,
    val achievements: List<Achievement>? = null
) {
    fun getActualData(): ProfileData {
        return data ?: ProfileData(
            userId = user_id ?: 0,
            username = username ?: "",
            name = name ?: "사용자",
            age = age ?: 0,
            height = height ?: 170.0f,
            weight = weight ?: 70.0f,
            gender = gender ?: "unknown",
            totalDays = totalDays ?: 0,
            totalKcal = totalKcal ?: 0,
            totalPunch = totalPunch ?: 0,
            achievements = achievements
        )
    }
}

/**
 * 프로필 데이터
 */
data class ProfileData(
    @SerializedName("user_id")
    val userId: Int,
    val username: String,
    val name: String,
    val age: Int,
    val height: Float,
    val weight: Float,
    val gender: String,

    @SerializedName("total_days")
    val totalDays: Int,
    @SerializedName("total_kcal")
    val totalKcal: Int,
    @SerializedName("total_punch")
    val totalPunch: Int,

    val achievements: List<Achievement>? = null,
    val email: String? = null,
    val phone: String? = null,
    @SerializedName("birth_date")
    val birthDate: String? = null,
    @SerializedName("profile_image")
    val profileImage: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("last_session_date")
    val lastSessionDate: String? = null
)

data class Achievement(
    val id: Int,
    val name: String,
    @SerializedName("is_completed")
    val isCompleted: Boolean,
    val description: String? = null,
    val icon: String? = null,
    @SerializedName("completed_at")
    val completedAt: String? = null
)