package com.example.studita.presentation.fragments.exercises

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import androidx.core.view.OneShotPreDrawListener
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.example.studita.R
import com.example.studita.domain.entity.UserDataData
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.model.ExerciseResultAnimation
import com.example.studita.presentation.view_model.ExercisesEndFragmentViewModel
import com.example.studita.presentation.view_model.ExercisesViewModel
import com.example.studita.presentation.views.CustomProgressBar
import com.example.studita.utils.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.exercises_result_layout.*
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
                initAnimation(userDataToAnimation, viewModel, percent, exercisesBonusCorrectCount)
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
                    8.dpToPx()
                )
                exercisesResultLayoutScrollView.setPadding(
                    exercisesResultLayoutScrollView.paddingLeft,
                    16.dpToPx(),
                    exercisesResultLayoutScrollView.paddingRight,
                    16.dpToPx()
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

            viewLifecycleOwner.lifecycleScope.launch {
                delay(300L)

                lastAnimation?.let {
                    if (lastAnimation.javaClass != anim.javaClass) {
                        when (anim) {
                            is ExerciseResultAnimation.AllCorrectBonus -> {

                                exercisesResultLayoutProgressBarText.text = resources.getString(
                                    R.string.exercises_XP_bonus,
                                    LevelUtils.ALL_CORRECT_BONUS
                                )
                            }
                            is ExerciseResultAnimation.LevelUPBonus -> {
                                exercisesResultLayoutProgressBarText.text = resources.getString(
                                    R.string.exercises_XP_bonus,
                                    LevelUtils.NEXT_LEVEL_BONUS * newLevels
                                )
                            }
                            is ExerciseResultAnimation.SequenceBonus -> {
                                exercisesResultLayoutProgressBarText.text = resources.getString(
                                    R.string.exercises_XP_bonus,
                                    LevelUtils.SEQUENCE_BONUS
                                )
                            }
                            is ExerciseResultAnimation.BonusExercisesBonus -> {
                                exercisesResultLayoutProgressBarText.text = resources.getString(
                                    R.string.exercises_XP_bonus,
                                    exercisesBonusObtainedXP
                                )
                            }
                        }
                        exercisesResultLayoutProgressBarText.animateFadeIn()
                    }
                }

                exercisesResultLayoutProgressBar.animateProgress(anim.to,
                    duration = resources.getInteger(R.integer.exercises_progress_XP_duration)
                        .toLong(), onAnimEnd = {
                        if (anim.to == 1F) {
                            animateLevelUp {
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

    private fun initAnimation(
        userData: UserDataData,
        viewModel: ExercisesViewModel,
        percent: Float,
        exercisesBonusCorrectCount: Int
    ) {

        exercisesResultLayoutProgressBar?.postExt<CustomProgressBar> {
            exercisesResultLayoutProgressBar.percentProgress =
                LevelUtils.getLevelProgressPercent(userData)
            startAnimation(userData, percent, viewModel.isTraining, exercisesBonusCorrectCount)
        }
        exercisesResultLayoutProgressBarText.text = resources.getString(
            R.string.exercises_XP_result,
            LevelUtils.percentToXP(percent, viewModel.isTraining)
        )
        updateTextLevels(userData.currentLevel)
        exercisesResultLayoutAnswersPercent.text = resources.getString(
            R.string.answers_percent,
            (percent * 100).toInt()
        )
    }

    private fun animateLevelUp(onAnimationEnd: () -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(200)
            exercisesResultLayoutProgressBar.clearProgress()
            exercisesResultLayoutProgressBarText.text = resources.getString(R.string.new_level)
            exercisesResultLayoutProgressBarText.animateFadeIn()
            updateTextLevels(exercisesResultLayoutCurrentLevel.text.toString().toInt() + 1)
            delay(500)
            onAnimationEnd.invoke()
        }
    }

    private fun updateTextLevels(currentLevel: Int) {
        exercisesResultLayoutCurrentLevel.text = currentLevel.toString()
        exercisesResultLayoutNextLevel.text = LevelUtils.getNextLevel(currentLevel).toString()
    }

}