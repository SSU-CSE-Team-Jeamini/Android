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

    private lateinit var tvCalorie: TextView
    private lateinit var tvHeight: TextView
    private lateinit var tvWeight: TextView

    private var currentTabType: TabType = TabType.CALORIE
    private var currentUsername: String? = null

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
        tvHeight = view.findViewById(R.id.tv_punch)
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
        currentUsername = MainActivity.username ?: TokenManager.getUserId(requireContext())

        if (currentUsername == null) {
            setDefaultValues()
            loadGraphData(TabType.CALORIE)
            return
        }

        // 서버에서 프로필 데이터 가져오기
        RetrofitClient.api.getProfile(currentUsername!!)
            .enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(
                    call: Call<ProfileResponse>,
                    response: Response<ProfileResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()!!

                        if (body.status == "success" || body.status == null) {
                            val profileData = body.getActualData()
                            updateUIWithServerData(profileData)
                        } else {
                            loadDummyDataAsFallback()
                        }
                    } else {
                        loadDummyDataAsFallback()
                    }
                    loadGraphData(TabType.CALORIE)
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    loadDummyDataAsFallback()
                    loadGraphData(TabType.CALORIE)
                }
            })
    }

    private fun updateUIWithServerData(data: ProfileData) {
        tvCalorie.text = "${data.totalKcal}kcal"
        tvHeight.text = "${data.height.toInt()}cm"
        tvWeight.text = "${data.weight.toInt()}kg"
    }

    private fun loadDummyDataAsFallback() {
        val currentUser = DummyUserStore.currentUser
        if (currentUser != null) {
            tvCalorie.text = "${currentUser.totalCalorie}kcal"
            tvHeight.text = "${currentUser.height.toInt()}cm"
            tvWeight.text = "${currentUser.weight.toInt()}kg"
            Toast.makeText(requireContext(), "서버 연결 실패 - 임시 데이터 사용", Toast.LENGTH_SHORT).show()
        } else {
            setDefaultValues()
        }
    }

    private fun setDefaultValues() {
        tvCalorie.text = "0kcal"
        tvHeight.text = "170cm"
        tvWeight.text = "70kg"
    }

    private fun loadGraphData(type: TabType) {
        if (currentUsername == null) {
            loadDummyGraphData(type)
            return
        }

        val endpoint = when (type) {
            TabType.CALORIE -> "calorie"
            TabType.PUNCH -> "punch"
            TabType.RANK -> "rank"
        }

        RetrofitClient.api.getGraphData(endpoint, currentUsername!!, "week")
            .enqueue(object : Callback<GraphResponse> {
                override fun onResponse(
                    call: Call<GraphResponse>,
                    response: Response<GraphResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()!!

                        if (body.status == "success" || body.status == null) {
                            val graphData = body.getActualGraphData()
                            if (graphData.isNotEmpty()) {
                                updateGraph(graphData, type)
                                return
                            }
                        }
                    }

                    loadDummyGraphData(type)
                }

                override fun onFailure(call: Call<GraphResponse>, t: Throwable) {
                    loadDummyGraphData(type)
                }
            })
    }

    private fun loadDummyGraphData(type: TabType) {
        val dummy = listOf(
            GraphData("2024-10-26", 300f),
            GraphData("2024-10-27", 270f),
            GraphData("2024-10-29", 380f),
            GraphData("2024-10-31", 460f),
            GraphData("2024-11-03", 410f),
            GraphData("2024-11-08", 530f)
        )

        updateGraph(dummy, type)
        Toast.makeText(requireContext(), "서버 연결 실패 - 임시 그래프 사용", Toast.LENGTH_SHORT).show()
    }

    private fun updateGraph(data: List<GraphData>, type: TabType) {
        tvPlaceholder.visibility = View.GONE
        graphContainer.removeAllViews()

        val chart = LineChart(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        graphContainer.addView(chart)

        val entries = data.mapIndexed { index, item ->
            Entry(index.toFloat(), item.value)
        }

        val colorMain = Color.parseColor("#205825")

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

        val labels = data.map { it.date }

        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            textSize = 10f
            granularity = 1f
            setDrawGridLines(false)
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val idx = value.toInt()
                    return if (idx in labels.indices) {
                        labels[idx].substring(5)
                    } else ""
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
}