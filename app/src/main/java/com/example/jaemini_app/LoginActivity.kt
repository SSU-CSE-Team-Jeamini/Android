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
                        Toast.makeText(
                            this@LoginActivity,
                            "서버 응답 없음 → 임시 로그인",
                            Toast.LENGTH_SHORT
                        ).show()
                        useDummyLogin(id, pw)
                        return
                    }

                    val body = response.body()!!

                    // ✅ 서버는 "success" 필드 사용
                    if (body.success == true) {
                        Toast.makeText(this@LoginActivity, "로그인 성공!", Toast.LENGTH_SHORT).show()

                        // 토큰 저장 (서버가 토큰을 안 보내주면 더미 토큰 사용)
                        val token = body.token ?: "server-token-${System.currentTimeMillis()}"
                        TokenManager.saveToken(this@LoginActivity, token)
                        TokenManager.saveUserId(this@LoginActivity, body.username ?: id)

                        // ✅ 서버에서 받은 데이터로 DummyUser 생성
                        // 프로필 정보는 나중에 /api/users/profile에서 가져옴
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
                        val errorMessage = body.message ?: "아이디 또는 비밀번호가 틀렸습니다"
                        Toast.makeText(
                            this@LoginActivity,
                            "$errorMessage → 임시 로그인",
                            Toast.LENGTH_SHORT
                        ).show()
                        useDummyLogin(id, pw)
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(
                        this@LoginActivity,
                        "서버 연결 실패 → 임시 로그인",
                        Toast.LENGTH_SHORT
                    ).show()

                    useDummyLogin(id, pw)
                }
            })
    }

    private fun useDummyLogin(id: String, pw: String) {
        // ① 더미 리스트에서 계정 찾기
        val matchedUser = DummyUserStore.getUser(id, pw)

        // ② 있으면 그 계정 그대로 사용
        if (matchedUser != null) {
            DummyUserStore.currentUser = matchedUser
            TokenManager.saveToken(this, "dummy-token")
            TokenManager.saveUserId(this, matchedUser.id)
            Toast.makeText(
                this,
                "더미 계정으로 로그인: ${matchedUser.nickname}",
                Toast.LENGTH_SHORT
            ).show()
            moveToHome(matchedUser.id)
            return
        }

        // ③ 없으면 입력값 기반으로 "임시 생성"
        val newUser = DummyUser(
            id = id,
            pw = pw,
            nickname = "${id}님",
            birth = null,
            age = null,
            height = 175.0f,
            weight = 70.0f,
            gender = null,
            totalCalorie = 1000,
            totalPunch = 200,
            totalDays = 5
        )

        DummyUserStore.currentUser = newUser
        TokenManager.saveToken(this, "dummy-token")
        TokenManager.saveUserId(this, newUser.id)
        Toast.makeText(
            this,
            "새 임시 계정 생성: ${newUser.nickname}",
            Toast.LENGTH_SHORT
        ).show()

        moveToHome(newUser.id)
    }

    private fun moveToHome(id: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("username", id)
        startActivity(intent)
        finish()
    }
}