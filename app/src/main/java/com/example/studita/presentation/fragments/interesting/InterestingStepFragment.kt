package com.example.studita.presentation.fragments.interesting

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.model.InterestingUiModelScreen
import com.example.studita.presentation.view_model.InterestingViewModel
import kotlinx.android.synthetic.main.interesting_start_screen_layout.*

class InterestingStepFragment : NavigatableFragment(R.layout.interesting_start_screen_layout){

    var interestingViewModel: InterestingViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        interestingViewModel = activity?.run {
            ViewModelProviders.of(this).get(InterestingViewModel::class.java)
        }
        interestingViewModel?.let {
            val startScreenModel = it.currentScreen as InterestingUiModelScreen.InterestingUiModelStepScreen

            if(startScreenModel.title != null)
                interestingStartScreenLayoutTitle.text = startScreenModel.title
            else
                interestingStartScreenLayoutTitle.visibility = View.GONE

            if(startScreenModel.subtitle != null)
                interestingStartScreenLayoutSubtitle.text = startScreenModel.subtitle
            else
                interestingStartScreenLayoutSubtitle.visibility = View.GONE

            interestingStartScreenDifficultyLayout.visibility = View.GONE
        }
    }

}