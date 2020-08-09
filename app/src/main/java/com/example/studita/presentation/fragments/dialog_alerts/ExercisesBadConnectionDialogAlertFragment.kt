package com.example.studita.presentation.fragments.dialog_alerts

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.fragments.base.BaseDialogFragment
import com.example.studita.presentation.view_model.ExercisesViewModel
import com.example.studita.utils.PrefsUtils
import kotlinx.android.synthetic.main.exercises_bad_connection_dialog_alert.*


class ExercisesBadConnectionDialogAlertFragment :
    BaseDialogFragment(R.layout.exercises_bad_connection_dialog_alert) {

    var exercisesViewModel: ExercisesViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }
        exercisesBadConnectionDialogLeftButton.setOnClickListener {
            dismiss()
            exercisesViewModel?.checkExerciseResult()
        }
        exercisesBadConnectionDialogRightButton.setOnClickListener {
            dismiss()
            exercisesViewModel?.waitingJob?.cancel()
            exercisesViewModel?.let {
                PrefsUtils.setOfflineMode(true)
                it.getExercises(it.chapterPartNumber)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (activity?.isDestroyed == false)
            (targetFragment as? DialogInterface.OnDismissListener)?.onDismiss(dialog)
    }

}