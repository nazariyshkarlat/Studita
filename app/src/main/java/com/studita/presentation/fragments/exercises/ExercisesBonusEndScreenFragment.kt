package com.studita.presentation.fragments.exercises

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.studita.R
import com.studita.presentation.fragments.base.NavigatableFragment
import com.studita.presentation.fragments.dialog_alerts.ExercisesBadConnectionDialogAlertFragment
import com.studita.presentation.view_model.ExercisesViewModel
import com.studita.utils.LanguageUtils
import com.studita.utils.navigateTo
import com.studita.utils.replace
import kotlinx.android.synthetic.main.exercises_bonus_end_layout.*

class ExercisesBonusEndScreenFragment : NavigatableFragment(R.layout.exercises_bonus_end_layout), DialogInterface.OnDismissListener {

    init {
        isLongAnim = true
    }

    val exercisesViewModel : ExercisesViewModel? by lazy {
        activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel?.let { vm ->
            vm.progressState.observe(
                viewLifecycleOwner,
                getProgressStateObserver(vm)
            )

            vm.showBadConnectionDialogAlertFragmentState.observe(
                viewLifecycleOwner,
                Observer { show ->
                    if (show && activity?.supportFragmentManager?.findFragmentByTag(
                            ExercisesBadConnectionDialogAlertFragment.BAG_CONNECTION_DIALOG) == null) {
                        activity?.supportFragmentManager?.let {
                            val dialogFragment = ExercisesBadConnectionDialogAlertFragment()
                                .apply {
                                    setTargetFragment(this@ExercisesBonusEndScreenFragment, 5325)
                                }
                            dialogFragment.show(
                                it,
                                ExercisesBadConnectionDialogAlertFragment.BAG_CONNECTION_DIALOG
                            )
                        }
                    }
                })
        }

        arguments?.let {
            exercisesBonusEndLayoutTitle.text = resources.getString(
                R.string.exercises_bonus_end_layout_title_template,
                it.getInt("OBTAINED_XP")
            )
            exercisesBonusEndLayoutSubtitle.text =
                LanguageUtils.getResourcesRussianLocale(view.context).getQuantityString(
                    R.plurals.exercises_bonus_end_layout_subtitle_plurals_template,
                    it.getInt("CORRECT_ANSWERS_COUNT"),
                    it.getInt("CORRECT_ANSWERS_COUNT")
                )
            exercisesBonusEndLayoutButton.setOnClickListener {
                exercisesViewModel?.endExercises()
            }
        }

    }


    override fun onDismiss(dialog: DialogInterface?) {
        exercisesViewModel?.let {
            if (it.chapterPartIsFullyCompleted()) {
                it.saveObtainedExercisesResult()
            }
        }
    }

    private fun getProgressStateObserver(exercisesViewModel: ExercisesViewModel) =
        Observer<Pair<Float, Boolean>> { pair ->
            val last = pair.second
            if (last) {
                exercisesBonusEndLayoutButton.setOnClickListener {  }
                exercisesViewModel.saveUserDataState.observe(viewLifecycleOwner, Observer {
                    val saved = it.first
                    if (saved) {
                        (activity as AppCompatActivity).replace(
                            exercisesViewModel.getExercisesEndFragment(
                                it.second
                            ), R.id.frameLayout,
                            addToBackStack = false
                        )
                    }
                })
            }
        }

}