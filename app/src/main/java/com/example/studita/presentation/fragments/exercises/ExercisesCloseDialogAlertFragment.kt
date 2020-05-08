package com.example.studita.presentation.fragments.exercises

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.fragments.base.BaseDialogFragment
import com.example.studita.presentation.view_model.ExercisesViewModel
import kotlinx.android.synthetic.main.exercises_close_dialog_alert.*

class ExercisesCloseDialogAlertFragment : BaseDialogFragment(R.layout.exercises_close_dialog_alert){

    var exercisesViewModel: ExercisesViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }
        exercisesCloseDialogLeftButton.setOnClickListener { dialog?.dismiss() }
        exercisesCloseDialogRightButton.setOnClickListener { activity?.finish() }
    }

}