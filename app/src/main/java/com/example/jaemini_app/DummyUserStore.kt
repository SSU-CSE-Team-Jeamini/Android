package com.example.jaemini_app

object DummyUserStore {

    var currentUser: DummyUser? = null

    private val dummyUsers = listOf(
        DummyUser(
            id = "test",
            pw = "1234",
            nickname = "테스트",
            birth = "1999-01-01",
            age = 25,
            height = 175.0f,
            weight = 70.0f,
            gender = "male",
            totalCalorie = 1200,
            totalPunch = 300,
            totalDays = 15
        ),
        DummyUser(
            id = "ssu",
            pw = "0000",
            nickname = "숭실대",
            birth = "2000-03-15",
            age = 24,
            height = 170.0f,
            weight = 68.0f,
            gender = "male",
            totalCalorie = 900,
            totalPunch = 250,
            totalDays = 8
        ),
        DummyUser(
            id = "gm",
            pw = "fitpunch",
            nickname = "경민",
            birth = "1998-07-20",
            age = 26,
            height = 180.0f,
            weight = 75.0f,
            gender = "male",
            totalCalorie = 1500,
            totalPunch = 500,
            totalDays = 22
        )
    )

    fun getUser(id: String, pw: String): DummyUser? {
        return dummyUsers.find { it.id == id && it.pw == pw }
    }

    fun getUserById(id: String): DummyUser? {
        return dummyUsers.find { it.id == id }
    }
}