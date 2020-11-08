package com.studita.presentation.fragments.interesting

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.studita.R
import com.studita.presentation.fragments.base.NavigatableFragment
import com.studita.presentation.model.InterestingUiModelScreen
import com.studita.presentation.view_model.InterestingViewModel
import kotlinx.android.synthetic.main.interesting_start_screen_layout.*

class InterestingStartScreenFragment :
    NavigatableFragment(R.layout.interesting_start_screen_layout) {

    private var interestingViewModel: InterestingViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        interestingViewModel = activity?.run {
            ViewModelProviders.of(this).get(InterestingViewModel::class.java)
        }
        interestingViewModel?.let {
            val startScreenModel =
                it.currentScreen as InterestingUiModelScreen.InterestingUiModelStartScreen
            interestingStartScreenLayoutTitle.text = startScreenModel.title
            interestingStartScreenLayoutSubtitle.text = startScreenModel.subtitle
            interestingStartScreenRatingBar.selectedStar = startScreenModel.difficultyLevel
        }
    }

}