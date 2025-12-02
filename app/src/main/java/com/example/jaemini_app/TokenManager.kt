package com.example.jaemini_app

import android.content.Context

object TokenManager {

    private const val PREF_NAME = "fitpunch_prefs"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_USER_ID = "user_id"  // 추가

    fun saveToken(context: Context, token: String) {
        val sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sp.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(context: Context): String? {
        val sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sp.getString(KEY_TOKEN, null)
    }

    fun clearToken(context: Context) {
        val sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sp.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_USER_ID)  // 사용자 ID도 함께 삭제
            .apply()
    }

    // 사용자 ID 저장
    fun saveUserId(context: Context, userId: String) {
        val sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sp.edit().putString(KEY_USER_ID, userId).apply()
    }

    // 사용자 ID 불러오기
    fun getUserId(context: Context): String? {
        val sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sp.getString(KEY_USER_ID, null)
    }
}