package com.example.studita.presentation.fragments.user_statistics.pages

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studita.domain.enum.UserStatisticsTime
import com.example.studita.domain.interactor.UserStatisticsStatus
import com.example.studita.presentation.fragments.user_statistics.UserStatFragment
import com.example.studita.presentation.utils.PrefsUtils
import com.example.studita.presentation.view_model.UserStatisticsViewModel

class UserStatMonthFragment : UserStatPageFragment(){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel?.let{
            if(savedInstanceState == null){
                PrefsUtils.getUserId()?.let { userId ->
                    PrefsUtils.getUserToken()?.let { userToken ->
                        it.getUserStatistics(userId, userToken, UserStatisticsTime.MONTH)
                    }
                }
            }
            it.userStatisticsMonthState.observe(viewLifecycleOwner, Observer { state ->
                if(state is UserStatisticsStatus.Success)
                    formView(state.result)
            })
            it.errorState.observe(viewLifecycleOwner, Observer{ message->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            })
        }
    }

}