package com.example.jaemini_app

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("api/users/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // ✅ username 파라미터 추가
    @GET("api/rankings")
    fun getRanking(@Query("username") username: String): Call<RankingResponse>

    // ✅ username 파라미터 추가
    @GET("api/users/profile")
    fun getProfile(@Query("username") username: String): Call<ProfileResponse>

    // ✅ username 파라미터 추가 (사용자 랭킹 정보)
    @GET("api/users/rank")
    fun getUserRank(@Query("username") username: String): Call<RankDataResponse>

    // ✅ username 파라미터 추가 (홈 데이터)
    @GET("api/sessions/current")
    fun getHomeData(@Query("username") username: String): Call<HomeResponse>

    // ✅ username과 period 파라미터 추가 (그래프 데이터)
    @GET("api/graph/{graph_type}")
    fun getGraphData(
        @Path("graph_type") graphType: String,
        @Query("username") username: String,
        @Query("period") period: String = "week"
    ): Call<GraphResponse>
}