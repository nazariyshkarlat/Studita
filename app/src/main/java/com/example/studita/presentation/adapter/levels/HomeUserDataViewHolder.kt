package com.example.studita.presentation.adapter.levels

import android.view.View
import com.example.studita.R
import com.example.studita.presentation.utils.getAppCompatActivity
import com.example.studita.presentation.utils.navigateTo
import com.example.studita.presentation.fragments.user_statistics.UserStatFragment
import com.example.studita.presentation.model.HomeRecyclerUiModel
import com.example.studita.presentation.utils.LevelUtils.getLevelXP
import com.example.studita.presentation.utils.LevelUtils.getNextLevel
import com.example.studita.presentation.utils.TimeUtils
import com.example.studita.presentation.utils.UserUtils
import kotlinx.android.synthetic.main.home_layout_user_data.view.*
import java.text.SimpleDateFormat
import java.util.*

class HomeUserDataViewHolder(view: View) : LevelsViewHolder<HomeRecyclerUiModel.HomeUserDataUiModel>(view){

    override fun bind(model: HomeRecyclerUiModel) {

        model as HomeRecyclerUiModel.HomeUserDataUiModel

        with(itemView) {
            UserUtils.userData?.let {
                homeLayoutUserDataXPLayoutMoreButton.setOnClickListener {
                    getAppCompatActivity()?.navigateTo(UserStatFragment(), R.id.frameLayout)
                }
                homeLayoutUserDataLevelLayoutCurrentLevel.text = it.currentLevel.toString()
                homeLayoutUserDataLevelLayoutNextLevel.text =
                    getNextLevel(it.currentLevel).toString()
                homeLayoutUserDataLevelLayoutXP.text = itemView.context.resources.getString(
                    R.string.current_level_XP,
                    it.currentLevelXP,
                    getLevelXP(it.currentLevel)
                )
                homeLayoutUserDataLevelLayoutProgressBar.percentProgress =
                    it.currentLevelXP / getLevelXP(it.currentLevel).toFloat()
                homeLayoutUserDataXPLayoutStreakDays.text =
                    itemView.context.resources.getString(R.string.streak_days, it.streakDays)
                homeLayoutUserDataXPLayoutFireIcon.isActivated = streakActivated(it.streakDate)
            }
        }
    }

    private fun streakActivated(streakDate: Date) = TimeUtils.getCalendarDayCount(Date(), streakDate) == 0L


}