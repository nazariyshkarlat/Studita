package com.example.studita.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.example.studita.R
import com.example.studita.presentation.fragments.base.BaseDialogFragment
import kotlinx.android.synthetic.main.exercises_close_dialog_alert.*

class ExercisesCloseDialogAlertFragment : BaseDialogFragment(R.layout.exercises_close_dialog_alert){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesCloseDialogLeftButton.setOnClickListener { dialog?.hide() }
        exercisesCloseDialogRightButton.setOnClickListener { activity?.finish() }
    }

}