package com.example.studita.presentation.fragments.exercises

import android.graphics.drawable.Animatable2
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.example.studita.R
import com.example.studita.presentation.fragments.base.BaseFragment
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

}