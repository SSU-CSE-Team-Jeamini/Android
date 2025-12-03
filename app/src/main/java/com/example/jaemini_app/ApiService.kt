package com.example.jaemini_app

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("api/users/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("api/rankings")
    fun getRanking(): Call<RankingResponse>

    @GET("api/users/profile")
    fun getProfile(): Call<ProfileResponse>

    // ✅ 추가: 상위 % 데이터
    @GET("api/users/rank")
    fun getUserRank(): Call<RankDataResponse>

    @GET("api/sessions/current")
    fun getHomeData(): Call<HomeResponse>

    @GET("api/{endpoint}")
    fun getGraphData(@Path("endpoint") endpoint: String): Call<GraphResponse>
}