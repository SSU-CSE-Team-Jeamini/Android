package com.example.jaemini_app

import android.content.Context

object TokenManager {

    private const val PREF_NAME = "fitpunch_prefs"
    private const val KEY_TOKEN = "jwt_token"

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
        sp.edit().remove(KEY_TOKEN).apply()
    }
}
