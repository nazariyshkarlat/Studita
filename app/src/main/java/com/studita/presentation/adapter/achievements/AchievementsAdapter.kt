package com.studita.presentation.adapter.achievements

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.studita.R
import com.studita.domain.entity.AchievementDataData
import com.studita.utils.makeView

class AchievementsAdapter(
    val items: List<AchievementDataData>,
    private val areImprovableAchievements: Boolean
) : RecyclerView.Adapter<AchievementViewHolder<*>>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AchievementViewHolder<out AchievementDataData> =
        if(areImprovableAchievements)
            ImprovableAchievementViewHolder(parent.makeView(R.layout.improvable_achievement_item))
        else
            NonImprovableAchievementViewHolder(parent.makeView(R.layout.non_improvable_achievement_item))

    override fun onBindViewHolder(
        holder: AchievementViewHolder<out AchievementDataData>,
        position: Int
    ) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}

abstract class AchievementViewHolder<T : AchievementDataData>(view: View) :
    RecyclerView.ViewHolder(view) {

    abstract fun bind(model: AchievementDataData)
}

