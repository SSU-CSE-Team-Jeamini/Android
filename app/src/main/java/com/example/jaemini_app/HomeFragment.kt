package com.example.jaemini_app

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var tabCalorie: TextView
    private lateinit var tabPunch: TextView
    private lateinit var tabRank: TextView
    private lateinit var tvPlaceholder: TextView
    private lateinit var graphContainer: FrameLayout

    // 통계 TextView들
    private lateinit var tvCalorie: TextView
    private lateinit var tvPunch: TextView  // 이제 키로 표시
    private lateinit var tvWeight: TextView

    private var currentTabType: TabType = TabType.CALORIE

    enum class TabType {
        CALORIE, PUNCH, RANK
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupTabs()
        loadHomeData()
    }

    private fun initViews(view: View) {
        tabCalorie = view.findViewById(R.id.tab_calorie)
        tabPunch = view.findViewById(R.id.tab_punch)
        tabRank = view.findViewById(R.id.tab_rank)

        tvPlaceholder = view.findViewById(R.id.tv_placeholder)
        graphContainer = view.findViewById(R.id.graph_container)

        tvCalorie = view.findViewById(R.id.tv_calorie)
        tvPunch = view.findViewById(R.id.tv_punch)  // 키 데이터
        tvWeight = view.findViewById(R.id.tv_weight)
    }

    private fun setupTabs() {
        setSelectedTab(tabCalorie, TabType.CALORIE)

        tabCalorie.setOnClickListener {
            setSelectedTab(tabCalorie, TabType.CALORIE)
            loadGraphData(TabType.CALORIE)
        }

        tabPunch.setOnClickListener {
            setSelectedTab(tabPunch, TabType.PUNCH)
            loadGraphData(TabType.PUNCH)
        }

        tabRank.setOnClickListener {
            setSelectedTab(tabRank, TabType.RANK)
            loadGraphData(TabType.RANK)
        }
    }

    private fun setSelectedTab(selected: TextView, type: TabType) {
        currentTabType = type
        val tabs = listOf(tabCalorie, tabPunch, tabRank)

        for (tab in tabs) {
            if (tab == selected) {
                tab.setBackgroundResource(R.drawable.tab_selected_bg)
                tab.setTextColor(Color.WHITE)
            } else {
                tab.setBackgroundResource(R.drawable.tab_unselected_bg)
                tab.setTextColor(Color.BLACK)
            }
        }
    }

    private fun loadHomeData() {
        // 더미 유저가 있으면 우선 사용
        val currentUser = DummyUserStore.currentUser
        if (currentUser != null) {
            updateUIWithDummyData(currentUser)
            loadGraphData(TabType.CALORIE)
            return
        }

        // 서버에서 데이터 가져오기
        RetrofitClient.api.getProfile()
            .enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(
                    call: Call<ProfileResponse>,
                    response: Response<ProfileResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()!!
                        updateUIWithServerData(data)
                    } else {
                        // 실패 시 기본값
                        setDefaultValues()
                    }
                    loadGraphData(TabType.CALORIE)
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    // 네트워크 실패 시 기본값
                    setDefaultValues()
                    loadGraphData(TabType.CALORIE)
                }
            })
    }

    private fun updateUIWithDummyData(user: DummyUser) {
        // 칼로리: 총 칼로리 표시
        tvCalorie.text = "${user.totalCalorie}kcal"

        // 키: height가 있으면 표시, 없으면 기본값
        val height = user.height ?: 170.0f
        tvPunch.text = "${height.toInt()}cm"

        // 체중
        tvWeight.text = "${user.weight}kg"
    }

    private fun updateUIWithServerData(data: ProfileResponse) {
        // 칼로리
        tvCalorie.text = "${data.totalKcal}kcal"

        // 키: 서버에서 height 데이터 사용
        val height = data.height ?: 170.0f
        tvPunch.text = "${height.toInt()}cm"

        // 체중
        val weight = data.weight ?: 70.0f
        tvWeight.text = "${weight.toInt()}kg"
    }

    private fun setDefaultValues() {
        tvCalorie.text = "0kcal"
        tvPunch.text = "170cm"
        tvWeight.text = "70kg"
    }

    private fun loadGraphData(type: TabType) {

        // -------------------------
        // 🔥 더미 그래프 데이터
        // -------------------------
        val dummy = listOf(
            GraphData("2024-10-26", 300f),
            GraphData("2024-10-27", 270f),
            GraphData("2024-10-29", 380f),
            GraphData("2024-10-31", 460f),
            GraphData("2024-11-03", 410f),
            GraphData("2024-11-08", 530f)
        )

        val response = GraphResponse(type.name.lowercase(), dummy)
        updateGraph(response, type)
    }

    private fun updateGraph(data: GraphResponse, type: TabType) {
        tvPlaceholder.visibility = View.GONE
        graphContainer.removeAllViews()

        val chart = LineChart(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        graphContainer.addView(chart)

        val entries = data.data.mapIndexed { index, item ->
            Entry(index.toFloat(), item.value)
        }

        val colorMain = when (type) {
            TabType.CALORIE -> Color.parseColor("#205825") // green
            TabType.PUNCH -> Color.parseColor("#205825")   // blue
            TabType.RANK -> Color.parseColor("#205825")    // orange
        }

        val dataSet = LineDataSet(entries, "").apply {
            color = colorMain
            lineWidth = 2.5f

            setDrawCircles(true)
            setCircleColor(colorMain)
            circleRadius = 5f

            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.2f

            setDrawFilled(true)
            fillColor = colorMain
            fillAlpha = 60

            valueTextSize = 0f
        }

        chart.data = LineData(dataSet)

        val labels = data.data.map { it.date }

        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            textSize = 10f
            granularity = 1f
            setDrawGridLines(false)
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val idx = value.toInt()
                    return if (idx in labels.indices) labels[idx].substring(5) else ""
                }
            }
        }

        chart.axisRight.isEnabled = false
        chart.axisLeft.textSize = 12f
        chart.axisLeft.setDrawGridLines(true)
        chart.axisLeft.gridColor = Color.parseColor("#DDDDDD")

        if (type == TabType.RANK) {
            chart.axisLeft.axisMinimum = 0f
            chart.axisLeft.axisMaximum = 100f
        }

        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setTouchEnabled(false)

        chart.animateX(700)
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}