package com.example.studita.presentation.fragments.user_statistics.pages

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.studita.domain.enum.UserStatisticsTime
import com.example.studita.domain.interactor.UserStatisticsStatus
import com.example.studita.presentation.utils.UserUtils

class UserStatMonthFragment : UserStatPageFragment(){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel?.let{viewModel->
            if(savedInstanceState == null){
                UserUtils.getUserTokenIdData()?.let{
                    viewModel.getUserStatistics(it, UserStatisticsTime.MONTH)
                }
            }
            viewModel.userStatisticsMonthState.observe(viewLifecycleOwner, Observer { state ->
                if(state is UserStatisticsStatus.Success)
                    formView(state.result)
            })
            viewModel.errorState.observe(viewLifecycleOwner, Observer{ message->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            })
        }
    }

}