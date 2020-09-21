package com.example.studita.presentation.fragments.user_statistics

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.domain.entity.UserStatisticsData
import com.example.studita.domain.interactor.UserStatisticsStatus
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.view_model.UserStatisticsPageViewModel
import com.example.studita.utils.TimeUtils
import kotlinx.android.synthetic.main.user_stat_page_layout.*

open class UserStatPageFragment : BaseFragment(R.layout.user_stat_page_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: UserStatisticsPageViewModel =
            ViewModelProviders.of(this).get(UserStatisticsPageViewModel::class.java)

        if (savedInstanceState == null) {
            arguments?.getInt("USER_ID")?.let {
                viewModel.getUserStatistics(it)
            }
        }

        val pageNumber = arguments?.getInt("PAGE_NUMBER") ?: 0
        viewModel.userStatisticsState.observe(viewLifecycleOwner, Observer {
            if (it is UserStatisticsStatus.Success)
                formView(it.results[pageNumber])
        })
    }

    private fun formView(userStatisticsData: UserStatisticsData) {
        userStatPageLayoutXPSubtitle.text = userStatisticsData.obtainedXP.toString()
        userStatPageLayoutTimeSubtitle.text = context?.let { context ->
            TimeUtils.styleTimeText(
                context,
                TimeUtils.getTimeText(
                    TimeUtils.getHours(userStatisticsData.obtainedTime) to resources.getString(R.string.hours),
                    TimeUtils.getMinutes(userStatisticsData.obtainedTime) to resources.getString(R.string.minutes),
                    TimeUtils.getSeconds(userStatisticsData.obtainedTime) to resources.getString(R.string.seconds)
                )
            )
        }
        userStatPageLayoutLessonsSubtitle.text = userStatisticsData.obtainedExercises.toString()
        userStatPageLayoutTrainingsSubtitle.text = userStatisticsData.obtainedTrainings.toString()
        (view as ViewGroup?)?.removeView(userStatPageLayoutProgressBar)
        userStatPageContentView.visibility = View.VISIBLE
    }

}