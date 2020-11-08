package com.studita.presentation.fragments.user_statistics

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.studita.R
import com.studita.domain.entity.UserStatisticsData
import com.studita.domain.interactor.UserStatisticsStatus
import com.studita.presentation.fragments.base.BaseFragment
import com.studita.presentation.fragments.error_fragments.InternetIsDisabledFragment
import com.studita.presentation.fragments.error_fragments.ServerProblemsFragment
import com.studita.presentation.listeners.ReloadPageCallback
import com.studita.presentation.view_model.UserStatisticsViewModel
import com.studita.utils.TimeUtils
import com.studita.utils.addFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.user_stat_page_layout.*

open class UserStatPageFragment : BaseFragment(R.layout.user_stat_page_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let{
            formView(Gson().fromJson(it.getString("USER_STAT"), object : TypeToken<UserStatisticsData>(){}.type))
        }

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
        userStatPageContentView.visibility = View.VISIBLE
    }

}