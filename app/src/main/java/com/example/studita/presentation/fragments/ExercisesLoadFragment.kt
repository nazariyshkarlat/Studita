package com.example.studita.presentation.fragments

import android.animation.Animator
import android.animation.AnimatorInflater
import android.graphics.drawable.Animatable2
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.example.studita.R
import com.example.studita.presentation.utils.replaceWithAnim
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.view_model.ExercisesViewModel
import kotlinx.android.synthetic.main.exercises_load_layout.view.*

class ExercisesLoadFragment : BaseFragment(R.layout.exercises_load_layout){

    var exercisesViewModel: ExercisesViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        startAnim(view)

        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        exercisesViewModel?.let {
            it.exercisesState.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer<Boolean> { done ->
                    if (done) {
                        (activity as AppCompatActivity).replaceWithAnim(ExercisesFragment(), R.id.frameLayout, 0, android.R.animator.fade_out)
                    }
                })

            it.errorState.observe(viewLifecycleOwner, Observer{ message->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            })
        }

    }

    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator? {
        if(!enter){
            val animator = AnimatorInflater.loadAnimator(context, nextAnim)
            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}

                override fun onAnimationEnd(animation: Animator?) {
                    exercisesViewModel?.startSecondsCounter()
                }

                override fun onAnimationCancel(animation: Animator?) {}

                override fun onAnimationStart(animation: Animator?) {}

            })
            return animator
        }else
            return null
    }

    private fun startAnim(view: View){
        with(view) {
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.M) {
                val listener = object : Animatable2.AnimationCallback() {
                    override fun onAnimationEnd(drawable: Drawable) {
                        exercisesLoadLayoutLoadImage.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.load_anim))
                        val anim = exercisesLoadLayoutLoadImage.drawable as Animatable2
                        anim.registerAnimationCallback(this)
                        anim.start()
                    }
                }
                val anim = exercisesLoadLayoutLoadImage.drawable as Animatable2

                anim.registerAnimationCallback(listener)
                anim.start()
            } else {
                val listener = object : Animatable2Compat.AnimationCallback() {
                    override fun onAnimationEnd(drawable: Drawable) {
                        exercisesLoadLayoutLoadImage.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.load_anim))
                        val anim = exercisesLoadLayoutLoadImage.drawable as Animatable2Compat
                        anim.registerAnimationCallback(this)
                        anim.start()
                    }
                }
                val anim = exercisesLoadLayoutLoadImage.drawable as AnimatedVectorDrawableCompat

                anim.registerAnimationCallback(listener)
                anim.start()
            }
        }
    }

}