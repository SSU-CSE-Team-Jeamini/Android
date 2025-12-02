package com.example.jaemini_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RankingFragment : Fragment() {

    private lateinit var tvMyRankValue: TextView
    private lateinit var tvMyPercent: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var rankingAdapter: RankingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ranking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        loadRankingData()
    }

    private fun initViews(view: View) {
        tvMyRankValue = view.findViewById(R.id.tv_my_rank_value)
        tvMyPercent = view.findViewById(R.id.tv_my_percent)
        recyclerView = view.findViewById(R.id.recycler_ranking)

        // RecyclerView 설정
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        rankingAdapter = RankingAdapter()
        recyclerView.adapter = rankingAdapter
    }

    private fun loadRankingData() {
        RetrofitClient.api.getRanking()
            .enqueue(object : Callback<RankingResponse> {
                override fun onResponse(
                    call: Call<RankingResponse>,
                    response: Response<RankingResponse>
                ) {
                    if (!response.isSuccessful) {
                        showError("서버 오류: ${response.code()}")
                        loadDummyData()  // ✅ 더미 데이터 로드
                        return
                    }

                    val body = response.body()
                    if (body == null) {
                        showError("데이터가 없습니다")
                        loadDummyData()  // ✅ 더미 데이터 로드
                        return
                    }

                    updateUI(body)
                }

                override fun onFailure(call: Call<RankingResponse>, t: Throwable) {
                    showError("네트워크 오류: ${t.message}")
                    loadDummyData()  // ✅ 더미 데이터 로드
                }
            })
    }

    private fun updateUI(data: RankingResponse) {
        tvMyRankValue.text = "${data.my_rank}위"
        tvMyPercent.text = "상위 ${data.my_percent}%"

        val allRankings = mutableListOf<RankingItem>()
        allRankings.addAll(data.top3)
        allRankings.addAll(data.list)

        rankingAdapter.submitList(allRankings)
    }

    // ✅ 더미 데이터 로드 함수 추가
    private fun loadDummyData() {
        // 내 순위 표시
        val currentUser = DummyUserStore.currentUser
        val myRank = if (currentUser != null) {
            when (currentUser.id) {
                "test" -> 4
                "ssu" -> 6
                "gm" -> 1
                else -> 10
            }
        } else {
            10
        }

        tvMyRankValue.text = "${myRank}위"
        tvMyPercent.text = "상위 ${(myRank * 10)}%"

        // 더미 랭킹 데이터
        val dummyRankings = listOf(
            RankingItem(1, "경민", 1500, 500),
            RankingItem(2, "김철수", 1350, 450),
            RankingItem(3, "이영희", 1200, 400),
            RankingItem(4, "테스트", 1200, 300),
            RankingItem(5, "박민수", 950, 280),
            RankingItem(6, "숭실대", 900, 250),
            RankingItem(7, "정다은", 850, 230),
            RankingItem(8, "최지훈", 800, 210)
        )

        rankingAdapter.submitList(dummyRankings)

        Toast.makeText(
            requireContext(),
            "더미 랭킹 데이터 표시 중",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}