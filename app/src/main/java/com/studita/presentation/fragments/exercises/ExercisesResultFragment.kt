package com.studita.presentation.fragments.exercises

import android.os.Bundle
import android.view.View
import androidx.core.view.OneShotPreDrawListener
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.studita.R
import com.studita.domain.entity.UserDataData
import com.studita.presentation.fragments.base.BaseFragment
import com.studita.presentation.model.ExerciseResultAnimation
import com.studita.presentation.view_model.ExercisesEndFragmentViewModel
import com.studita.presentation.view_model.ExercisesViewModel
import com.studita.presentation.views.ProgressBar
import com.studita.utils.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.exercises_result_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ExercisesResultFragment : BaseFragment(R.layout.exercises_result_layout) {

    var exercisesViewModel: ExercisesViewModel? = null
    var exercisesEndFragmentViewModel: ExercisesEndFragmentViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        exercisesEndFragmentViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesEndFragmentViewModel::class.java)
        }

        arguments?.let {
            val percent = it.getFloat("ANSWERS_PERCENT")
            val userDataToAnimation = Gson().fromJson<UserDataData>(
                it.getString("OLD_USER_DATA"),
                object : TypeToken<UserDataData>() {}.type
            )

            val exercisesBonusCorrectCount = it.getInt("EXERCISES_BONUS_CORRECT_COUNT")

            exercisesViewModel?.let { viewModel ->

                formView(if(savedInstanceState == null) userDataToAnimation else UserUtils.userData, viewModel, percent, exercisesBonusCorrectCount)

            }
        }

        checkScrollable()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            exercisesViewModel?.showExercisesEndTextButton(true)
            checkScrollable()
        }
    }

    private fun checkScrollable() {
        OneShotPreDrawListener.add(exercisesResultLayoutScrollView) {
            val isScrollable: Boolean =
                exercisesResultLayoutScrollView.height < exercisesResultLayoutScrollView.getChildAt(
                    0
                ).height + exercisesResultLayoutScrollView.paddingTop + exercisesResultLayoutScrollView.paddingBottom
            if (isScrollable) {
                exercisesEndFragmentViewModel?.setScrollViewDividerAndPadding(
                    R.drawable.divider_top_drawable,
                    8F.dpToPx()
                )
                exercisesResultLayoutScrollView.setPadding(
                    exercisesResultLayoutScrollView.paddingLeft,
                    16F.dpToPx(),
                    exercisesResultLayoutScrollView.paddingRight,
                    16F.dpToPx()
                )
            }
        }
    }

    private fun startAnimation(
        userData: UserDataData,
        percent: Float,
        isTraining: Boolean,
        exercisesBonusCorrectCount: Int
    ) {
        val anims = LevelUtils.getExerciseResultAnimation(
            userData,
            percent,
            isTraining,
            exercisesBonusCorrectCount
        )
        animateProgress(
            anims.iterator(),
            anims.filter { it.to == 1F }.count(),
            null,
            LevelUtils.getObtainedExercisesBonusXP(exercisesBonusCorrectCount, isTraining)
        )
    }

    private fun animateProgress(
        animations: Iterator<ExerciseResultAnimation>,
        newLevels: Int,
        lastAnimation: ExerciseResultAnimation?,
        exercisesBonusObtainedXP: Int
    ) {
        if (animations.hasNext()) {

            val anim = animations.next()

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                delay(300L)

                lastAnimation?.let {
                    if (lastAnimation.javaClass != anim.javaClass) {
                        exercisesResultLayoutProgressBarText.text = getProgressBarText(anim, newLevels, exercisesBonusObtainedXP, arguments!!.getInt("OBTAINED_XP"))
                        exercisesResultLayoutProgressBarText.animateFadeIn()
                    }
                }

                exercisesResultLayoutProgressBar.animateProgress(anim.to,
                    duration = resources.getInteger(R.integer.exercises_progress_XP_duration)
                        .toLong(), onAnimEnd = {
                        if (anim.to == 1F) {
                            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                                animateLevelUp()
                                animateProgress(
                                    animations,
                                    newLevels,
                                    anim,
                                    exercisesBonusObtainedXP
                                )
                            }
                        } else {
                            animateProgress(animations, newLevels, anim, exercisesBonusObtainedXP)
                        }
                    })
            }
        }
    }

    private fun getProgressBarText(anim: ExerciseResultAnimation, newLevels: Int, exercisesBonusObtainedXP: Int, obtainedXP: Int) =
        when (anim) {
            is ExerciseResultAnimation.AllCorrectBonus -> {

                resources.getString(
                    R.string.exercises_XP_bonus,
                    LevelUtils.ALL_CORRECT_BONUS
                )
            }
            is ExerciseResultAnimation.LevelUPBonus -> {
                resources.getString(
                    R.string.exercises_XP_bonus,
                    LevelUtils.NEXT_LEVEL_BONUS * newLevels
                )
            }
            is ExerciseResultAnimation.SequenceBonus -> {
                resources.getString(
                    R.string.exercises_XP_bonus,
                    LevelUtils.SEQUENCE_BONUS
                )
            }
            is ExerciseResultAnimation.BonusExercisesBonus -> {
                resources.getString(
                    R.string.exercises_XP_bonus,
                    exercisesBonusObtainedXP
                )
            }
            is ExerciseResultAnimation.ObtainedXP -> resources.getString(
                R.string.exercises_XP_result,
                obtainedXP
            )
        }

    private fun formView(
        userData: UserDataData,
        viewModel: ExercisesViewModel,
        percent: Float,
        exercisesBonusCorrectCount: Int
    ) {

        exercisesResultLayoutProgressBar?.postExt<ProgressBar> {
            exercisesResultLayoutProgressBar.currentProgress = LevelUtils.getLevelProgressPercent(userData)

            if(UserUtils.userData != userData) {
                startAnimation(userData, percent, viewModel.isTraining, exercisesBonusCorrectCount)
                exercisesResultLayoutProgressBarText.text = resources.getString(
                    R.string.exercises_XP_result,
                    LevelUtils.percentToXP(percent, viewModel.isTraining)
                )
            }else
                exercisesResultLayoutProgressBarText.text = resources.getString(
                    R.string.exercises_XP_result,
                    arguments!!.getInt("OBTAINED_XP")
                )
        }
        updateTextLevels(userData.currentLevel)
        exercisesResultLayoutAnswersPercent.text = resources.getString(
            R.string.answers_percent,
            (percent * 100).toInt()
        )
    }

    private fun animateLevelUp() {
            exercisesResultLayoutProgressBar.currentProgress = 0F
            exercisesResultLayoutProgressBarText.text = resources.getString(R.string.new_level)
            exercisesResultLayoutProgressBarText.animateFadeIn()
            updateTextLevels(exercisesResultLayoutCurrentLevel.text.toString().toInt() + 1)
    }

    private fun updateTextLevels(currentLevel: Int) {
        exercisesResultLayoutCurrentLevel.text = currentLevel.toString()
        exercisesResultLayoutNextLevel.text = LevelUtils.getNextLevel(currentLevel).toString()
    }

}