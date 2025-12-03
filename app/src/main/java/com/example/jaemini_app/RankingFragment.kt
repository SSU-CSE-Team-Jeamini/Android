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

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        rankingAdapter = RankingAdapter()
        recyclerView.adapter = rankingAdapter
    }

    private fun loadRankingData() {
        // username 가져오기
        val username = MainActivity.username ?: TokenManager.getUserId(requireContext())

        if (username == null) {
            Toast.makeText(requireContext(), "사용자 정보 없음", Toast.LENGTH_SHORT).show()
            loadDummyData()
            return
        }

        // 서버에서 랭킹 데이터 가져오기
        RetrofitClient.api.getRanking(username)
            .enqueue(object : Callback<RankingResponse> {
                override fun onResponse(
                    call: Call<RankingResponse>,
                    response: Response<RankingResponse>
                ) {
                    if (!response.isSuccessful) {
                        showError("서버 오류: ${response.code()}")
                        loadDummyData()
                        return
                    }

                    val body = response.body()
                    if (body == null) {
                        showError("데이터가 없습니다")
                        loadDummyData()
                        return
                    }

                    // status 확인
                    if (body.status == "success" || body.status == null) {
                        // getActualData() 메서드로 데이터 가져오기
                        val rankingData = body.getActualData()
                        updateUI(rankingData)
                    } else {
                        showError(body.message ?: "랭킹 로드 실패")
                        loadDummyData()
                    }
                }

                override fun onFailure(call: Call<RankingResponse>, t: Throwable) {
                    showError("네트워크 오류: ${t.message}")
                    loadDummyData()
                }
            })
    }

    private fun updateUI(data: RankingData) {
        // 내 순위 표시
        tvMyRankValue.text = "${data.myRank}위"
        tvMyPercent.text = "상위 ${data.myPercent}%"

        // 전체 랭킹 리스트 (top3 + list)
        val allRankings = mutableListOf<RankingItem>()
        allRankings.addAll(data.top3)
        allRankings.addAll(data.list)

        rankingAdapter.submitList(allRankings)
    }

    private fun loadDummyData() {
        // 더미 데이터 (서버 연결 실패 시에만 사용)
        val currentUser = DummyUserStore.currentUser
        val myRank = when (currentUser?.id) {
            "test" -> 4
            "ssu" -> 6
            "gm" -> 1
            else -> 10
        }

        tvMyRankValue.text = "${myRank}위"
        tvMyPercent.text = "상위 ${(myRank * 10)}%"

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
            "서버 연결 실패 - 임시 데이터 사용",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}