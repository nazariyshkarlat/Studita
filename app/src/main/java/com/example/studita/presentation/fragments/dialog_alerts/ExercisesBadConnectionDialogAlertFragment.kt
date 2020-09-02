package com.example.studita.presentation.fragments.dialog_alerts

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.fragments.base.BaseDialogFragment
import com.example.studita.presentation.view_model.ExercisesViewModel
import com.example.studita.utils.PrefsUtils
import kotlinx.android.synthetic.main.dialog_alert_layout.*


class ExercisesBadConnectionDialogAlertFragment :
    BaseDialogFragment(R.layout.dialog_alert_layout) {

    var exercisesViewModel: ExercisesViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setText(resources.getString(R.string.exercises_bad_connection_dialog_alert_title),
            resources.getString(R.string.exercises_bad_connection_dialog_alert_subtitle),
            resources.getString(R.string.wait),
            resources.getString(R.string.offline_mode)
        )

        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }
        dialogAlertLeftButton.setOnClickListener {
            dismiss()
            exercisesViewModel?.checkExerciseResult()
        }
        dialogAlertRightButton.setOnClickListener {
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