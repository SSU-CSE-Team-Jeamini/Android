package com.example.jaemini_app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("SplashActivity", "onCreate started")

        try {
            setContentView(R.layout.activity_splash)
            Log.d("SplashActivity", "Layout loaded successfully")
        } catch (e: Exception) {
            Log.e("SplashActivity", "Layout loading failed", e)
            Toast.makeText(this, "Layout error: ${e.message}", Toast.LENGTH_LONG).show()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            try {
                val token = TokenManager.getToken(this)
                val userId = TokenManager.getUserId(this)

                Log.d("SplashActivity", "Token: $token, UserId: $userId")

                if (token != null && userId != null) {
                    Log.d("SplashActivity", "User found, restoring...")
                    restoreDummyUser(userId)

                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("username", userId)
                    Log.d("SplashActivity", "Starting MainActivity")
                    startActivity(intent)
                } else {
                    Log.d("SplashActivity", "No token, going to LoginActivity")
                    startActivity(Intent(this, LoginActivity::class.java))
                }

                finish()
            } catch (e: Exception) {
                Log.e("SplashActivity", "Error during navigation", e)
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }, 1000)
    }

    private fun restoreDummyUser(userId: String) {
        try {
            Log.d("SplashActivity", "Restoring user: $userId")
            val dummyUser = DummyUserStore.getUserById(userId)

            if (dummyUser != null) {
                DummyUserStore.currentUser = dummyUser
                Log.d("SplashActivity", "User restored: ${dummyUser.nickname}")
            } else {
                // ✅ 수정: height 추가, weight를 Float로 변경
                DummyUserStore.currentUser = DummyUser(
                    id = userId,
                    pw = "",
                    nickname = "${userId}님",
                    birth = null,
                    age = null,
                    height = 175.0f,        // ✅ 추가 (Float)
                    weight = 70.0f,         // ✅ Float로 변경 (70 → 70.0f)
                    gender = null,
                    totalCalorie = 1000,
                    totalPunch = 200,
                    totalDays = 5
                )
                Log.d("SplashActivity", "New user created: ${userId}님")
            }
        } catch (e: Exception) {
            Log.e("SplashActivity", "Error restoring user", e)
        }
    }
}