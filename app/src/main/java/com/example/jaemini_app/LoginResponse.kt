package com.example.jaemini_app

import com.google.gson.annotations.SerializedName

/**
 * 로그인 API 응답 모델 (서버 실제 응답에 맞춤)
 *
 * 서버 응답:
 * {
 *   "success": true,
 *   "user_id": 1,
 *   "username": "test",
 *   "name": "테스트"
 * }
 *
 * 또는 실패 시:
 * {
 *   "success": false,
 *   "message": "Invalid username or password"
 * }
 */
data class LoginResponse(
    val success: Boolean? = null,          // 서버는 success 필드 사용
    val message: String? = null,           // 에러 메시지

    @SerializedName("user_id")
    val userId: Int? = null,
    val username: String? = null,
    val name: String? = null,

    // 서버가 토큰을 보내주지 않으므로 null 허용
    val token: String? = null
)