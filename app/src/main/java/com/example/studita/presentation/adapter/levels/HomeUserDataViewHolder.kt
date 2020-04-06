package com.example.studita.presentation.adapter.levels

import android.view.View
import com.example.studita.R
import com.example.studita.presentation.utils.getAppCompatActivity
import com.example.studita.presentation.utils.navigateTo
import com.example.studita.presentation.fragments.user_statistics.UserStatFragment
import com.example.studita.presentation.model.HomeRecyclerUiModel
import kotlinx.android.synthetic.main.home_layout_user_data.view.*

class HomeUserDataViewHolder(view: View) : LevelsViewHolder<HomeRecyclerUiModel.HomeUserDataUiModel>(view){

    override fun bind(model: HomeRecyclerUiModel) {

        val firstLevelXP = itemView.context.resources.getInteger(R.integer.first_level_XP)

        model as HomeRecyclerUiModel.HomeUserDataUiModel
        itemView.homeLayoutUserDataXPLayoutMoreButton.setOnClickListener {
            itemView.getAppCompatActivity()?.navigateTo(UserStatFragment(), R.id.frameLayout)
        }
        itemView.homeLayoutUserDataLevelLayoutCurrentLevel.text = model.currentLevel.toString()
        itemView.homeLayoutUserDataLevelLayoutNextLevel.text = (model.currentLevel + 1).toString()
        itemView.homeLayoutUserDataLevelLayoutXP.text = itemView.context.resources.getString(R.string.current_level_XP, model.currentLevelXP, firstLevelXP)
        itemView.homeLayoutUserDataLevelLayoutProgressBar.percentProgress = model.currentLevelXP/firstLevelXP.toFloat()
        itemView.homeLayoutUserDataXPLayoutStreakDays.text = itemView.context.resources.getString(R.string.streak_days, model.streakDays)
    }

}