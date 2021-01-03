package com.studita.presentation.fragments.interesting

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.studita.R
import com.studita.presentation.fragments.base.NavigatableFragment
import com.studita.presentation.model.InterestingUiModelScreen
import com.studita.presentation.view_model.InterestingViewModel
import kotlinx.android.synthetic.main.interesting_step_layout.*

class InterestingStepFragment : NavigatableFragment(R.layout.interesting_step_layout) {

    var interestingViewModel: InterestingViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        interestingViewModel = activity?.run {
            ViewModelProviders.of(this).get(InterestingViewModel::class.java)
        }

        interestingViewModel?.let {
            val stepScreenModel =
                it.currentScreen as InterestingUiModelScreen.InterestingUiModelStepScreen

            if (stepScreenModel.title != null)
                interestingStepLayoutTitle.text = stepScreenModel.title
            else
                interestingStepLayoutTitle.visibility = View.GONE

            if (stepScreenModel.subtitle != null)
                interestingStepLayoutSubtitle.text = stepScreenModel.subtitle
            else
                interestingStepLayoutSubtitle.visibility = View.GONE
        }
    }

}