package com.example.jaemini_app

import android.os.Bundle
import android.view.LayoutInflater  // ← 추가
import android.view.View  // ← 추가
import android.view.ViewGroup  // ← 추가
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment  // ← Fragment import
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RankingFragment : Fragment() {  // ← Fragment 상속으로 변경

    private lateinit var tvMyRankValue: TextView
    private lateinit var tvMyPercent: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var rankingAdapter: RankingAdapter

    // ← onCreateView 추가 (레이아웃 inflate)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ranking, container, false)
    }

    // ← onViewCreated로 변경
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)  // ← view 파라미터 추가
        loadRankingData()
    }

    private fun initViews(view: View) {  // ← view 파라미터 추가
        tvMyRankValue = view.findViewById(R.id.tv_my_rank_value)  // ← view.findViewById
        tvMyPercent = view.findViewById(R.id.tv_my_percent)
        recyclerView = view.findViewById(R.id.recycler_ranking)

        // RecyclerView 설정
        recyclerView.layoutManager = LinearLayoutManager(requireContext())  // ← requireContext()
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
                        return
                    }

                    val body = response.body()
                    if (body == null) {
                        showError("데이터가 없습니다")
                        return
                    }

                    updateUI(body)
                }

                override fun onFailure(call: Call<RankingResponse>, t: Throwable) {
                    showError("네트워크 오류: ${t.message}")
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

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()  // ← requireContext()
    }
}