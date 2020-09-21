package com.example.studita.presentation.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.Gravity.END
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.core.view.contains
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.example.studita.R
import com.example.studita.presentation.listeners.SwipeGestureListener
import com.example.studita.utils.isContains
import com.example.studita.utils.makeView
import kotlinx.android.synthetic.main.exercises_help_layout.view.*

@SuppressLint("ClickableViewAccessibility")
class ExerciseParentView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var helpIsEnabled = false
    private val helpView: View by lazy{this.makeView(R.layout.exercises_help_layout)}
    private var helpViewState: HelpViewState = HelpViewState.StateHidden("", "")

    private val helpGestureDetector = GestureDetector(context, object : SwipeGestureListener(){
        override fun onSwipeLeft() {
            if (helpViewState is HelpViewState.StateClosed) {
                helpViewState = HelpViewState.StateOpen(helpViewState.leftText, helpViewState.rightText)
                openView()
            }
        }
    })

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    fun enableHelpView(){
        helpIsEnabled = true

        if(!contains(helpView)) {
            helpView.layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                topMargin = resources.getDimension(R.dimen.exercisesHelpViewMarginTop).toInt()
                gravity = END
            }
            this.addView(helpView)
            hideView(withAnimation = false)
            helpView.exercisesHelpLayoutButton.setOnClickListener {
                if (helpViewState is HelpViewState.StateOpen)
                    closeView()
                else
                    openView()
            }
        }
    }

    fun disableHelpView(){
        helpIsEnabled = false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if(helpIsEnabled) {
            if(ev.action == ACTION_DOWN) {
                if (helpViewState is HelpViewState.StateOpen && !clickIsOnHelpView(ev.rawX.toInt(), ev.rawY.toInt()))
                    closeView()
            }
            helpGestureDetector.onTouchEvent(ev)
            false
        }else
            super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if(helpIsEnabled) {
            if(event.action == ACTION_DOWN) {
                if (helpViewState is HelpViewState.StateOpen && !clickIsOnHelpView(event.rawX.toInt(), event.rawY.toInt()))
                    closeView()
            }
            helpGestureDetector.onTouchEvent(event)
        }else
            super.onInterceptTouchEvent(event)
    }

    fun openView(withAnimation: Boolean = true){
        helpView.measure(0, 0)
        helpViewState = HelpViewState.StateOpen(helpViewState.leftText, helpViewState.rightText)
        if(withAnimation) {
            helpView.animate()
                .translationX(0F)
                .setInterpolator(FastOutSlowInInterpolator())
                .setListener(null)
                .start()
        }else{
            helpView.translationX = 0F
        }
        animateIconChange(false)
    }

    fun closeView(withAnimation: Boolean = true){
        helpViewState = HelpViewState.StateClosed(helpViewState.leftText, helpViewState.rightText)
        helpView.measure(0, 0)
        if(withAnimation) {
            helpView.animate()
                .translationX((helpView.measuredWidth - helpView.exercisesHelpLayoutButton.measuredWidth).toFloat())
                .setInterpolator(FastOutSlowInInterpolator())
                .setListener(null)
                .start()
        }else{
            helpView.translationX = (helpView.measuredWidth - helpView.exercisesHelpLayoutButton.measuredWidth).toFloat()
        }
        animateIconChange(true)
    }

    fun hideView(withAnimation: Boolean = true){
        helpViewState = HelpViewState.StateHidden(helpViewState.leftText, helpViewState.rightText)
        helpView.measure(0, 0)
        if(withAnimation) {
            helpView.animate()
                .translationX((helpView.measuredWidth).toFloat())
                .setInterpolator(FastOutSlowInInterpolator())
                .setListener(null)
                .start()
        }else{
            helpView.translationX = (helpView.measuredWidth).toFloat()
        }
    }

    fun showView(withAnimation: Boolean = true){
        helpViewState = HelpViewState.StateClosed(helpViewState.leftText, helpViewState.rightText)
        helpView.measure(0, 0)
        if(withAnimation) {
            helpView.animate()
                .translationX((helpView.measuredWidth - helpView.exercisesHelpLayoutButton.measuredWidth).toFloat())
                .setInterpolator(FastOutSlowInInterpolator())
                .setListener(null)
                .start()
        }else{
            helpView.translationX = (helpView.measuredWidth - helpView.exercisesHelpLayoutButton.measuredWidth).toFloat()
        }
    }

    private fun changeIcon(close: Boolean){
        if (close)
            helpView.exercisesHelpLayoutIcon.setImageResource(R.drawable.ic_help_outline_accent)
        else
            helpView.exercisesHelpLayoutIcon.setImageResource(R.drawable.ic_chevron_right_accent)
    }

    private fun animateIconChange(close: Boolean){
        helpView.exercisesHelpLayoutIcon.animate()
            .scaleX(if(close) 1F else 0.6F)
            .scaleY(if(close) 1F else 0.6F)
            .alpha(0.3F)
            .setDuration(100)
            .setListener(object : AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator?) {
                    changeIcon(close)
                    if(close){
                        helpView.exercisesHelpLayoutIcon.scaleX = 0.6F
                        helpView.exercisesHelpLayoutIcon.scaleY = 0.6F
                    }else{
                        helpView.exercisesHelpLayoutIcon.scaleX = 1F
                        helpView.exercisesHelpLayoutIcon.scaleY = 1F
                    }
                    helpView.exercisesHelpLayoutIcon.animate()
                        .scaleX(1F)
                        .scaleY(1F)
                        .alpha(1F)
                        .setListener(null)
                        .setDuration(100)
                        .start()
                }
            })
            .start()
    }

    fun setText(leftText: String, rightText: String){
        helpView.exercisesHelpLayoutLeftTextView.text = leftText
        helpView.exercisesHelpLayoutRightTextView.text = rightText
        if(helpViewState is HelpViewState.StateHidden) {
            hideView(false)
            showView()
        }else{
            showView(false)
        }
    }


    private fun clickIsOnHelpView(x: Int, y: Int) =  helpView.isContains(
        x,
        y
    )

    sealed class HelpViewState(open val leftText: String, open val rightText: String){
        class StateHidden(override val leftText: String, override val rightText: String) : HelpViewState(leftText, rightText)
        class StateClosed(override val leftText: String, override val rightText: String) : HelpViewState(leftText, rightText)
        class StateOpen(override val leftText: String, override val rightText: String) : HelpViewState(leftText, rightText)
    }
}