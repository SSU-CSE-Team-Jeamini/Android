package com.example.jaemini_app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Splash 디자인 XML 적용
        setContentView(R.layout.activity_splash)

        // 자동 로그인 확인
        val token = TokenManager.getToken(this)

        if (token != null) {
            // 로그인 유지됨 → 바로 메인 이동
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // 로그인 필요 → 로그인 화면 이동
            startActivity(Intent(this, LoginActivity::class.java))
        }

        finish()   // 뒤로가기 방지
    }
}
