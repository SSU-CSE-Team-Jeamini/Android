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

class HomeFragment : Fragment() {

    private lateinit var tabCalorie: TextView
    private lateinit var tabPunch: TextView
    private lateinit var tabRank: TextView
    private lateinit var tvPlaceholder: TextView
    private lateinit var graphContainer: FrameLayout

    // 통계 TextView들
    private lateinit var tvCalorie: TextView
    private lateinit var tvPunch: TextView
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
        tvPunch = view.findViewById(R.id.tv_punch)
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
        // 지금은 임시 데이터
        tvCalorie.text = "520"
        tvPunch.text = "350"
        tvWeight.text = "72kg"

        loadGraphData(TabType.CALORIE)
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
