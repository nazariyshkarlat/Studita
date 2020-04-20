package com.example.studita.presentation.fragments.user_statistics.pages

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.domain.entity.UserStatisticsData
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.utils.TimeUtils
import com.example.studita.presentation.view_model.UserStatisticsViewModel
import kotlinx.android.synthetic.main.user_stat_page_layout.*

open class UserStatPageFragment : BaseFragment(R.layout.user_stat_page_layout){

    protected var viewModel: UserStatisticsViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.run{
            ViewModelProviders.of(this).get(UserStatisticsViewModel::class.java)
        }
    }

    fun formView(userStatisticsData: UserStatisticsData){
        userStatPageLayoutXPSubtitle.text = userStatisticsData.obtainedXP.toString()
        userStatPageLayoutTimeSubtitle.text = context?.let {context->
            TimeUtils.styleTimeText(
                context,
                TimeUtils.getTimeText(TimeUtils.getHours(userStatisticsData.obtainedTime) to resources.getString(R.string.hours),
                    TimeUtils.getMinutes(userStatisticsData.obtainedTime) to resources.getString(R.string.minutes),
                    TimeUtils.getSeconds(userStatisticsData.obtainedTime) to  resources.getString(R.string.seconds))
            )
        }
        userStatPageLayoutLessonsSubtitle.text = userStatisticsData.obtainedExercises.toString()
        userStatPageLayoutTrainingsSubtitle.text = userStatisticsData.obtainedTrainings.toString()
        userStatPageLayoutAchievementsSubtitle.text = userStatisticsData.obtainedAchievements.toString()
        ((userStatPageLayoutProgressBar).parent as ViewGroup).removeView(userStatPageLayoutProgressBar)
        userStatPageContentView.visibility = View.VISIBLE
    }

}