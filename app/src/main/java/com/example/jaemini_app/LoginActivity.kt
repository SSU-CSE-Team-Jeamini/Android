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

        // 자동 로그인 체크
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
                        // ❌ 서버 오류 - 로그인 차단
                        Toast.makeText(
                            this@LoginActivity,
                            "서버 연결 오류입니다. 잠시 후 다시 시도해주세요.",
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    }

                    val body = response.body()!!

                    // ✅ 서버는 "success" 필드 사용
                    if (body.success == true) {
                        Toast.makeText(this@LoginActivity, "로그인 성공!", Toast.LENGTH_SHORT).show()

                        // 토큰 저장
                        val token = body.token ?: "server-token-${System.currentTimeMillis()}"
                        TokenManager.saveToken(this@LoginActivity, token)
                        TokenManager.saveUserId(this@LoginActivity, body.username ?: id)

                        // ✅ 서버에서 받은 데이터로 DummyUser 생성
                        DummyUserStore.currentUser = DummyUser(
                            id = body.username ?: id,
                            pw = pw,
                            nickname = body.name ?: "사용자",
                            birth = null,
                            age = null,
                            height = 175.0f,
                            weight = 70.0f,
                            gender = null,
                            totalCalorie = 0,
                            totalPunch = 0,
                            totalDays = 0
                        )

                        moveToHome(body.username ?: id)
                    } else {
                        // ❌ 로그인 실패 - 로그인 차단
                        val errorMessage = body.message ?: "아이디 또는 비밀번호가 틀렸습니다"
                        Toast.makeText(
                            this@LoginActivity,
                            errorMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    // ❌ 네트워크 오류 - 로그인 차단
                    Toast.makeText(
                        this@LoginActivity,
                        "네트워크 연결 오류입니다.\n인터넷 연결을 확인해주세요.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun moveToHome(id: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("username", id)
        startActivity(intent)
        finish()
    }
}