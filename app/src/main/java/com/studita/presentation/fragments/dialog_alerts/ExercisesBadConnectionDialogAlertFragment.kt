package com.studita.presentation.fragments.dialog_alerts

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.ViewModelProviders
import com.studita.R
import com.studita.presentation.fragments.base.BaseDialogFragment
import com.studita.presentation.view_model.ExercisesViewModel
import com.studita.presentation.views.CustomSnackbar
import com.studita.utils.PrefsUtils
import com.studita.utils.ThemeUtils
import kotlinx.android.synthetic.main.dialog_alert_layout.*


class ExercisesBadConnectionDialogAlertFragment :
    BaseDialogFragment(R.layout.dialog_alert_layout) {

    companion object{
        const val BAG_CONNECTION_DIALOG = "BAG_CONNECTION_DIALOG"
    }

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

            if(exercisesViewModel?.chapterPartIsFullyCompleted() == true)
                exercisesViewModel?.saveObtainedExercisesResult()
            else
                exercisesViewModel?.checkExerciseResult()
        }
        dialogAlertRightButton.setOnClickListener {
            dismiss()
            exercisesViewModel?.let {
                PrefsUtils.setOfflineMode(true)
                CustomSnackbar(context!!).show(
                    resources.getString(R.string.enable_offline_mode_snackbar),
                    ColorUtils.compositeColors(
                        ThemeUtils.getAccentLiteColor(context!!),
                        ContextCompat.getColor(context!!, R.color.white)
                    ),
                    ContextCompat.getColor(context!!, R.color.black),
                )

                if(exercisesViewModel?.chapterPartIsFullyCompleted() == true)
                    exercisesViewModel?.saveObtainedExercisesResult()
                else
                    it.getExercises()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (activity?.isDestroyed == false)
            (targetFragment as? DialogInterface.OnDismissListener)?.onDismiss(dialog)
    }

}