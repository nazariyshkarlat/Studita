package com.studita.presentation.fragments.dialog_alerts

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.studita.R
import com.studita.presentation.fragments.base.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_alert_layout.*

class ExercisesCloseDialogAlertFragment :
    BaseDialogFragment(R.layout.dialog_alert_layout) {

  /*  var exercisesViewModel: ExercisesViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setText(resources.getString(R.string.exercises_close_dialog_alert_title),
            resources.getString(R.string.exercises_close_dialog_alert_subtitle),
            resources.getString(R.string.cancel),
            resources.getString(R.string.leave)
        )
        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }
        dialogAlertLeftButton.setOnClickListener { dialog?.dismiss() }
        dialogAlertRightButton.setOnClickListener {
            dismiss()
            activity?.finish()
        }
    }*/

}