package com.example.jaemini_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RankingAdapter : RecyclerView.Adapter<RankingAdapter.RankingViewHolder>() {

    private var items: List<RankingItem> = emptyList()

    fun submitList(newItems: List<RankingItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ranking, parent, false)
        return RankingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class RankingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val layoutRankCircle: LinearLayout = itemView.findViewById(R.id.layout_rank_circle)
        private val tvRank: TextView = itemView.findViewById(R.id.tv_rank)
        private val tvName: TextView = itemView.findViewById(R.id.tv_name)
        private val tvKcal: TextView = itemView.findViewById(R.id.tv_kcal)
        private val tvPunch: TextView = itemView.findViewById(R.id.tv_punch)

        fun bind(item: RankingItem) {
            // 순위에 따라 배경 색상 변경
            val backgroundRes = when (item.rank) {
                1 -> R.drawable.rank_circle_gold      // 금색
                2 -> R.drawable.rank_circle_silver    // 은색
                3 -> R.drawable.rank_circle_bronze    // 동색
                else -> R.drawable.rank_circle_bg     // 기본 색상 (4~8위)
            }
            layoutRankCircle.setBackgroundResource(backgroundRes)

            // 데이터 바인딩
            tvRank.text = item.rank.toString()
            tvName.text = item.name
            tvKcal.text = "🔥 ${item.kcal} kcal"
            tvPunch.text = "🥊 ${item.punchCount}회"
        }
    }
}