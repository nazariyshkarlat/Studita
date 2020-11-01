package com.example.studita.presentation.adapter.levels

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.example.studita.R
import com.example.studita.domain.entity.UserData
import com.example.studita.presentation.activities.UserStatActivity
import com.example.studita.presentation.model.HomeRecyclerUiModel
import com.example.studita.utils.*
import com.example.studita.utils.LevelUtils.getLevelXP
import com.example.studita.utils.LevelUtils.getNextLevel
import kotlinx.android.synthetic.main.home_layout_user_data.view.*
import java.util.*

class HomeUserDataViewHolder(view: View, private val lifecycleOwner: LifecycleOwner) :
    LevelsViewHolder<HomeRecyclerUiModel.HomeUserDataUiModel>(view) {

    override fun bind(model: HomeRecyclerUiModel) {

        model as HomeRecyclerUiModel.HomeUserDataUiModel

        with(itemView) {
            homeLayoutUserDataXPLayoutMoreButton.setOnClickListener {
                getAppCompatActivity()?.startActivity<UserStatActivity>("USER_ID" to UserUtils.userData.userId)
            }
            
            UserUtils.userDataLiveData.observe(lifecycleOwner, androidx.lifecycle.Observer {
                homeLayoutUserDataLevelLayoutCurrentLevel.text =
                    it.currentLevel.toString()
                homeLayoutUserDataLevelLayoutNextLevel.text =
                    getNextLevel(it.currentLevel).toString()
                homeLayoutUserDataLevelLayoutXP.text = itemView.context.resources.getString(
                    R.string.current_level_XP,
                    it.currentLevelXP,
                    getLevelXP(it.currentLevel)
                )
                homeLayoutUserDataLevelLayoutProgressBar.currentProgress = it.currentLevelXP / getLevelXP(it.currentLevel).toFloat()
                homeLayoutUserDataXPLayoutStreakDays.text =
                    LanguageUtils.getResourcesRussianLocale(itemView.context)?.getQuantityString(
                        R.plurals.streak_plurals,
                        it.streakDays,
                        it.streakDays
                    )
                homeLayoutUserDataXPLayoutFireIcon.isActivated =
                    streakActivated(it.streakDatetime)
            })
        }
    }

    private fun streakActivated(streakDate: Date) =
        TimeUtils.getCalendarDayCount(Date(), streakDate) == 0L


}