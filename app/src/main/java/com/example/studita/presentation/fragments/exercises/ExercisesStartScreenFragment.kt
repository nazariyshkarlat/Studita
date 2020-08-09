package com.example.studita.presentation.fragments.exercises

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.view_model.ExercisesViewModel
import kotlinx.android.synthetic.main.exercises_start_screen_layout.*

class ExercisesStartScreenFragment : BaseFragment(R.layout.exercises_start_screen_layout) {

    var exercisesViewModel: ExercisesViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }
        exercisesViewModel?.let {
            val startScreenModel = it.exercisesResponseData.exercisesStartScreen
            exercisesStartScreenLayoutTitle.text = startScreenModel.title
            exercisesStartScreenLayoutSubtitle.text = startScreenModel.subtitle

            when (it.chapterPartNumber) {
                1 -> exercisesStartScreenLayoutImage.setImageResource(R.drawable.ic_chapter_part_one_start_screen_image)
                2 -> exercisesStartScreenLayoutImage.setImageResource(R.drawable.ic_chapter_part_two_start_screen_image)
                else -> exercisesStartScreenLayoutImage.visibility = View.GONE
            }
        }
    }

}