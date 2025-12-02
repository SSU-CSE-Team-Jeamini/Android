package com.example.jaemini_app

object DummyUserStore {

    var currentUser: DummyUser? = null

    private val dummyUsers = listOf(
        DummyUser("test", "1234", "테스트", 70, 1200, 300, 15),
        DummyUser("ssu", "0000", "숭실대", 68, 900, 250, 8),
        DummyUser("gm", "fitpunch", "경민", 75, 1500, 500, 22)
    )

    fun getUser(id: String, pw: String): DummyUser? {
        return dummyUsers.find { it.id == id && it.pw == pw }
    }

    // ID만으로 사용자 찾기 (자동 로그인용)
    fun getUserById(id: String): DummyUser? {
        return dummyUsers.find { it.id == id }
    }
}