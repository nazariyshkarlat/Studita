package com.example.studita.presentation.fragments.exercises

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import androidx.core.view.OneShotPreDrawListener
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.domain.entity.UserDataData
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.utils.*
import com.example.studita.presentation.view_model.ExercisesEndFragmentViewModel
import com.example.studita.presentation.view_model.ExercisesViewModel
import kotlinx.android.synthetic.main.exercises_result_layout.*


class ExercisesResultFragment : BaseFragment(R.layout.exercises_result_layout){

    var exercisesViewModel: ExercisesViewModel? = null
    var exercisesEndFragmentViewModel: ExercisesEndFragmentViewModel? = null

    private val ANIMATION_SCALE_FROM_VALUE = 1.2f
    private val ANIMATION_ALPHA_FROM_VALUE = 0.3f
    private val ANIMATION_FADE_IN_DURATION = 500

    lateinit var userData: UserDataData

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        exercisesEndFragmentViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesEndFragmentViewModel::class.java)
        }

        val percent : Float? = arguments?.getFloat("ANSWERS_PERCENT")

        exercisesViewModel?.let {viewModel->
            UserUtils.oldUserData?.let { oldUserData ->
                this.userData = oldUserData
                percent?.let {
                    exercisesResultLayoutProgressBar.percentProgress = LevelUtils.getLevelProgressPercent(oldUserData, oldUserData.currentLevelXP)
                    animateLevelXP(
                        exercisesResultLayoutProgressBar.percentProgress +
                        LevelUtils.getLevelProgressPercent(
                            oldUserData,
                            if(viewModel.isTraining)
                                LevelUtils.TRAINING_XP
                            else
                                LevelUtils.getXPFromPercent(it)
                        )
                    )

                    exercisesResultLayoutAnswersPercent.text = resources.getString(
                        R.string.answers_percent,
                        (it*100).toInt()
                    )
                    println(percent)
                    exercisesResultLayoutProgressBarText.text = resources.getString(
                        R.string.exercises_XP_result,
                        if(viewModel.isTraining)
                            LevelUtils.TRAINING_XP
                        else
                            LevelUtils.getXPFromPercent(it)
                    )
                }

                exercisesResultLayoutCurrentLevel.text = oldUserData.currentLevel.toString()
                exercisesResultLayoutNextLevel.text =
                    (LevelUtils.getNextLevel(oldUserData.currentLevel)).toString()
            }
        }

        checkScrollable()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden){
            exercisesViewModel?.showExercisesEndTextButton(true)
            checkScrollable()
        }
    }

    private fun checkScrollable(){
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

    private fun animateLevelXP(percent: Float){
        exercisesResultLayoutProgressBar.animateProgress(percent,
            duration = resources.getInteger(R.integer.exercises_progress_XP_duration)
                .toLong(), delay = 300L, onAnimEnd = {
                if(percent >= 1F) {

                    exercisesResultLayoutProgressBarText.text = resources.getString(R.string.new_level)
                    exercisesResultLayoutProgressBarText.animateFadeIn()
                    updateTextLevels()

                    animateNewLevelProgress(percent-1F){
                        exercisesResultLayoutProgressBarText.text = resources.getString(R.string.exercises_XP_bonus, LevelUtils.NEXT_LEVEL_BONUS)
                        exercisesResultLayoutProgressBarText.animateFadeIn()
                        animateBonusProgress()
                    }

                }
            })
    }

    private fun animateNewLevelProgress(percent: Float, onAnimationEnd: () -> Unit){
        exercisesResultLayoutProgressBar.clearProgress()
        exercisesResultLayoutProgressBar.animateProgress(percent, duration = resources.getInteger(R.integer.exercises_progress_XP_duration).toLong(), onAnimEnd = onAnimationEnd)
    }

    private fun animateBonusProgress(){
        exercisesResultLayoutProgressBar.animateProgress(exercisesResultLayoutProgressBar.percentProgress + LevelUtils.getLevelProgressPercent(userData, LevelUtils.NEXT_LEVEL_BONUS), duration = resources.getInteger(R.integer.exercises_progress_XP_duration).toLong())
    }

    private fun View.animateFadeIn(delay: Long = 0) {
        val alphaAnimator: ValueAnimator? = getAlphaAnimator(ANIMATION_ALPHA_FROM_VALUE, 1f)
        val scaleAnimator: ValueAnimator? =
            getScaleAnimator(ANIMATION_SCALE_FROM_VALUE, 1f)
        val animatorSet = AnimatorSet()
        animatorSet.startDelay = delay
        animatorSet.playTogether(alphaAnimator, scaleAnimator)
        animatorSet.duration = ANIMATION_FADE_IN_DURATION.toLong()
        animatorSet.start()
    }

    private fun updateTextLevels(){
        exercisesResultLayoutCurrentLevel.text = LevelUtils.getNextLevel(userData.currentLevel).toString()
        exercisesResultLayoutNextLevel.text = LevelUtils.getNextLevel( LevelUtils.getNextLevel(userData.currentLevel)).toString()
    }

}