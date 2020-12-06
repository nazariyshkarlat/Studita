package com.studita.presentation.fragments.exercises

import android.graphics.drawable.Animatable2
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.studita.R
import com.studita.presentation.fragments.base.BaseFragment
import com.studita.presentation.view_model.ExercisesViewModel
import com.studita.presentation.views.CustomSnackbar
import com.studita.utils.PrefsUtils
import com.studita.utils.ThemeUtils
import kotlinx.android.synthetic.main.exercises_load_layout.*

open class LoadFragment : BaseFragment(R.layout.exercises_load_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startAnim()
    }

    private fun startAnim() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            startHighApiAnimation(exercisesLoadLayoutLoadImage)
        } else {
            startLowApiAnimation(exercisesLoadLayoutLoadImage)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun startHighApiAnimation(view: ImageView) {
        val listener =
            object : Animatable2.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable) {
                    view.setImageDrawable(
                        AppCompatResources.getDrawable(
                            view.context,
                            R.drawable.load_anim
                        )
                    )
                    val anim = view.drawable as Animatable2
                    anim.registerAnimationCallback(this)
                    anim.start()
                }
            }
        val anim = view.drawable as Animatable2

        anim.registerAnimationCallback(listener)
        anim.start()
    }

    private fun startLowApiAnimation(view: ImageView) {
        val listener = object : Animatable2Compat.AnimationCallback() {
            override fun onAnimationEnd(drawable: Drawable) {
                view.setImageDrawable(
                    AppCompatResources.getDrawable(
                        view.context,
                        R.drawable.load_anim
                    )
                )
                val anim = view.drawable as Animatable2Compat
                anim.registerAnimationCallback(this)
                anim.start()
            }
        }
        val anim = view.drawable as AnimatedVectorDrawableCompat

        anim.registerAnimationCallback(listener)
        anim.start()
    }

    protected fun formBadConnectionButton(onOfflineModeEnabled: () -> Unit) {

        exercisesLoadLayoutTipTextView.text = resources.getString(R.string.issues_with_connecting)
        exercisesLoadLayoutButton.visibility = View.VISIBLE
        exercisesLoadLayoutButton.text = resources.getString(R.string.to_offline_mode)
        exercisesLoadLayoutButton.setOnClickListener {
            PrefsUtils.setOfflineMode(true)
            CustomSnackbar(context!!).show(
                resources.getString(R.string.enable_offline_mode_snackbar),
                ColorUtils.compositeColors(
                    ThemeUtils.getAccentLiteColor(context!!),
                    ContextCompat.getColor(context!!, R.color.white)
                ),
                ContextCompat.getColor(context!!, R.color.black)
            )
            onOfflineModeEnabled.invoke()
        }


        exercisesLoadLayoutBottomSection.alpha = 0F

        exercisesLoadLayoutBottomSection.animate().alpha(1F).setDuration(
            resources.getInteger(R.integer.exercises_load_layout_bottom_section_alpha_anim_duration)
                .toLong()
        ).start()
    }

}