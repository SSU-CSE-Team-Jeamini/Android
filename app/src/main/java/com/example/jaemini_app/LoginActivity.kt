package com.example.jaemini_app

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var etId: EditText
    private lateinit var etPw: EditText
    private lateinit var btnLogin: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val token = TokenManager.getToken(this)
        if (token != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        etId = findViewById(R.id.et_id)
        etPw = findViewById(R.id.et_pw)
        btnLogin = findViewById(R.id.btn_login)

        btnLogin.setOnClickListener {
            val id = etId.text.toString().trim()
            val pw = etPw.text.toString().trim()

            if (id.isEmpty() || pw.isEmpty()) {
                Toast.makeText(this, "아이디와 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sendLoginRequest(id, pw)
        }
    }

    private fun sendLoginRequest(id: String, pw: String) {
        val request = LoginRequest(id, pw)

        RetrofitClient.api.login(request)
            .enqueue(object : Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (!response.isSuccessful || response.body() == null) {
                        Toast.makeText(
                            this@LoginActivity,
                            "서버 응답 없음 → 임시 로그인 진행",
                            Toast.LENGTH_SHORT
                        ).show()
                        moveToHome(id)
                        return
                    }

                    val body = response.body()!!
                    if (body.status == "success") {
                        Toast.makeText(this@LoginActivity, "로그인 성공!", Toast.LENGTH_SHORT).show()
                        moveToHome(id)
                    } else {
                        Toast.makeText(this@LoginActivity, "아이디 또는 비밀번호가 틀렸습니다", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    // 서버가 아예 없거나 끊어져 있을 때 → 임시 로그인 허용
                    Toast.makeText(
                        this@LoginActivity,
                        "서버 연결 실패 → 임시 로그인",
                        Toast.LENGTH_SHORT
                    ).show()

                    moveToHome(id)
                }
            })
    }

    private fun moveToHome(id: String) {
        val intent = Intent(this, MainActivity::class.java)  // ← HomeFragment → MainActivity로 변경!
        intent.putExtra("username", id)
        startActivity(intent)
        finish()  // LoginActivity 종료
    }
}