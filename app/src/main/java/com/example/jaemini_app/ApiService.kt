package com.example.jaemini_app

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    // 로그인 API
    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // 랭킹 API
    @GET("ranking")
    fun getRanking(): Call<RankingResponse>

    // 프로필 API
    @GET("profile")
    fun getProfile(): Call<ProfileResponse>

    // 홈 데이터 API
    @GET("home")
    fun getHomeData(): Call<HomeResponse>

    // 그래프 데이터 API
    @GET("{endpoint}")
    fun getGraphData(@Path("endpoint") endpoint: String): Call<GraphResponse>
}