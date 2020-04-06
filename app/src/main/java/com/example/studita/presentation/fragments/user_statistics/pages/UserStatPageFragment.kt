package com.example.studita.presentation.fragments.user_statistics.pages

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.domain.entity.UserStatisticsData
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.utils.TimeUtils
import com.example.studita.presentation.view_model.UserStatisticsViewModel
import kotlinx.android.synthetic.main.exercises_detailed_stat_layout.*
import kotlinx.android.synthetic.main.user_stat_page_layout.*

open class UserStatPageFragment : BaseFragment(R.layout.user_stat_page_layout){

    protected var viewModel: UserStatisticsViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.run {
            ViewModelProviders.of(this).get(UserStatisticsViewModel::class.java)
        }
    }

    fun formView(userStatisticsData: UserStatisticsData){
        userStatPageLayoutXPSubtitle.text = userStatisticsData.obtainedXP.toString()
        userStatPageLayoutTimeSubtitle.text = context?.let {context->
            TimeUtils.styleTimeText(
                context,
                TimeUtils.getTimeText(TimeUtils.getHours(userStatisticsData.obtained_time.toInt()) to resources.getString(R.string.hours),
                    TimeUtils.getMinutes(userStatisticsData.obtained_time.toInt()) to resources.getString(R.string.minutes),
                    TimeUtils.getSeconds(userStatisticsData.obtained_time.toInt()) to  resources.getString(R.string.seconds))
            )
        }
        userStatPageLayoutLessonsSubtitle.text = userStatisticsData.obtained_lessons.toString()
        userStatPageLayoutTrainingsSubtitle.text = userStatisticsData.obtained_trainings.toString()
        userStatPageLayoutAchievementsSubtitle.text = userStatisticsData.obtained_achievements.toString()
    }

}