package com.example.studita.presentation.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.updateLayoutParams
import androidx.core.widget.TextViewCompat.setTextAppearance
import com.example.studita.R
import com.example.studita.utils.*
import com.google.android.flexbox.*
import java.lang.Math.abs


class ArrangeNumbersView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr){

    companion object{
        val correctAnswers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
    }

    private var answers = arrayOfNulls<Int?>(10)
    var variants = correctAnswers

    private var onNewAnswerListener: OnNewAnswerListener? = null
    var correctAnswersCount = 0

    private val topFlexboxLayout: FlexboxLayout
    private val bottomFlexboxLayout: FlexboxLayout

    init {
        orientation = VERTICAL
        topFlexboxLayout = formTopFlexbox()
        bottomFlexboxLayout = formBottomFlexbox()

        addView(FrameLayout(context).apply {
            addView(topFlexboxLayout)
            addView(bottomFlexboxLayout)
            this.post{
                bottomFlexboxLayout.updateLayoutParams<FrameLayout.LayoutParams> {
                    height = bottomFlexboxLayout.measuredHeight + topFlexboxLayout.measuredHeight
                }
            }
        })
    }

    private fun formTopFlexbox(): FlexboxLayout{

        val flexbox = formFlexbox().apply {
            layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            setPadding(16.dpToPx(), 16.dpToPx(), 16.dpToPx(), 16.dpToPx())
            background = ContextCompat.getDrawable(context, R.drawable.main_blocks_rectangle)
        }

        variants.forEachIndexed { index,  numberText ->
            val item = TextView(context).apply {
                setTextAppearance(this, R.style.Medium18)
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                gravity = Gravity.CENTER
                text = numberText.toString()
                setPadding(16.dpToPx(), 14.dpToPx(), 16.dpToPx(), 14.dpToPx())
                this.formTopChildItem(answers, index)
            }
            flexbox.addView(item)
        }

        return flexbox
    }


    private fun formBottomFlexbox(): FlexboxLayout{

        val flexbox = formFlexbox().apply {
            alignContent = AlignContent.FLEX_END
            alignItems = AlignItems.FLEX_END
            setPadding(16.dpToPx(), 32.dpToPx(), 16.dpToPx(), 0)
        }

        correctAnswers.forEach {
            val item = TextView(context).apply {
                setTextAppearance(this, R.style.Medium18)
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                gravity = Gravity.CENTER
                background = ContextCompat.getDrawable(context, R.drawable.arrange_numbers_bottom_item_background)
                setPadding(16.dpToPx(), 14.dpToPx(), 16.dpToPx(), 14.dpToPx())
                text = it.toString()
                setChildOnTouch()
            }
            flexbox.addView(item)
        }

        return flexbox
    }

    fun setUpVariants(variants: List<Int>, onNewAnswerListener: OnNewAnswerListener? = null){
        this.variants = variants
        variants.forEachIndexed {idx, variant ->
            (bottomFlexboxLayout.getChildAt(idx) as TextView).text = variant.toString()
        }

        if(onNewAnswerListener != null){
            this.onNewAnswerListener = onNewAnswerListener
        }
    }

    private fun TextView.formTopChildItem(answers: Array<Int?>, index: Int){
        when{
            (index == 0 || (index == answers.indexOfFirst { it == null })) && answers[index] == null -> {
                background = ContextCompat.getDrawable(context, R.drawable.arrange_numbers_open_item_background)
                setTextColor(ContextCompat.getColor(context, android.R.color.transparent))
            }
            answers[index] != null-> {
                text = answers[index].toString()
                background = ContextCompat.getDrawable(context,
                    if(correctAnswers[index] == answers[index])
                        R.drawable.arrange_numbers_true_answered_item_background
                    else
                        R.drawable.arrange_numbers_false_answered_item_background)
                setTextColor(ContextCompat.getColor(context, R.color.white87))
            }
            else -> {
                background = ContextCompat.getDrawable(context, R.drawable.arrange_numbers_locked_item_background)
                setTextColor(ContextCompat.getColor(context, android.R.color.transparent))
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun View.setChildOnTouch() {

        setOnTouchListener(object : OnTouchListener {

            private var dX: Float = 0F
            private var dY: Float = 0F

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        val maxTransZ =
                            bottomFlexboxLayout.children.maxBy { it.translationZ }!!.translationZ
                        v.translationZ = maxTransZ + 1
                        dX = v.x - event.rawX
                        dY = v.y - event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {

                        val newX = event.rawX + dX

                        if (bottomFlexboxLayout.childIsInParentX(
                                newX.toInt(),
                                (newX + v.measuredWidth).toInt()
                            )
                        ) {
                            v.x = newX
                        } else {
                            if (newX < 0F) {
                                v.x = 0F
                            } else {
                                v.x =
                                    (bottomFlexboxLayout.measuredWidth - v.measuredWidth).toFloat()
                            }
                        }
                        val newY = event.rawY + dY
                        if (bottomFlexboxLayout.childIsInParentY(
                                newY.toInt(),
                                (newY + v.measuredHeight).toInt()
                            )
                        ) {
                            v.y = newY
                        } else {
                            if (newY < 0F) {
                                v.y = 0F
                            } else {
                                v.y =
                                    (bottomFlexboxLayout.measuredHeight - v.measuredHeight).toFloat()
                            }
                        }
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        when {
                            v.isClick(event) -> {
                                onSelect(true)
                            }
                            v.isAboveCell() -> {
                                onSelect(false)
                            }
                            else -> {
                                v.animate().translationX(0F).translationY(0F).start()
                            }
                        }
                        return true
                    }
                    else -> return false
                }
            }

        })
    }

    private fun View.isAboveCell(): Boolean{
        val currentIndex = answers.indexOfFirst { it == null }
        return  this.isContainsAnotherView(topFlexboxLayout.getChildAt(currentIndex))
    }

    private fun View.onSelect(isClick: Boolean){
        isEnabled = false
        isClickable = false
        val currentIndex = answers.indexOfFirst { it == null }
        answers[currentIndex] = variants[bottomFlexboxLayout.indexOfChild(this)]
        if(isFalseAnswer(currentIndex)) {
            answers[variants[bottomFlexboxLayout.indexOfChild(this)] - 1] =
                correctAnswers[currentIndex]
        }
        val currentAnswers = answers.clone()

        val newPlaceView = topFlexboxLayout.getChildAt(currentIndex)
        animate()
            .x(newPlaceView.x)
            .y(newPlaceView.y)
            .setDuration(300L)
            .setListener(object : AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    Handler().postDelayed({
                        visibility = View.INVISIBLE
                        updateAllTopChildren(currentAnswers)
                    }, 100)
                }
            })
            .start()

        if(isFalseAnswer(currentIndex)) {
            val falseView = bottomFlexboxLayout.getChildAt(variants.indexOf(correctAnswers[currentIndex])).apply {
                isEnabled = false
                isClickable = false
            }
            val newPlaceFalseView = topFlexboxLayout.getChildAt(variants[bottomFlexboxLayout.indexOfChild(this)]-1)
            falseView
                .animate()
                .x(newPlaceFalseView.x)
                .y(newPlaceFalseView.y)
                .setListener(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        Handler().postDelayed({
                            falseView.visibility = View.INVISIBLE
                        }, 100L)
                    }
                })
                .start()
        }else{
            correctAnswersCount++
            onNewAnswerListener?.onTrueAnswer(correctAnswersCount)
        }

        if(answers.count { it != null } == variants.size){
            onNewAnswerListener?.onViewIsFilled(correctAnswersCount)
        }
    }

    private fun updateAllTopChildren(answers: Array<Int?>){
        topFlexboxLayout.children.forEach{
            (it as TextView).formTopChildItem(answers, topFlexboxLayout.indexOfChild(it))
        }
    }

    private fun updateAllBottomChildren(){
        answers.forEach {
            if(it != null) {
                bottomFlexboxLayout.getChildAt(variants.indexOf(it)).apply {
                    visibility = View.INVISIBLE
                    isEnabled = false
                    isClickable = false
                }
            }
        }
    }

    private fun formFlexbox() = FlexboxLayout(context).apply {
        flexWrap = FlexWrap.WRAP
        clipToPadding = false
        justifyContent = JustifyContent.CENTER
        setDividerDrawable(GradientDrawable().apply {
            setSize(16.dpToPx(), 16.dpToPx())
        })
        setShowDivider(FlexboxLayout.SHOW_DIVIDER_MIDDLE)
    }

    private fun isFalseAnswer(index: Int) = answers[index] != null && (correctAnswers[index] != answers[index])

    @Suppress("UNCHECKED_CAST")
    private class SavedState : BaseSavedState {
        var variants: List<Int> = emptyList()
        var answers = arrayOfNulls<Int?>(10)

        internal constructor(superState: Parcelable?) : super(superState) {}
        private constructor(`in`: Parcel) : super(`in`) {
            `in`.readList(variants, variants.javaClass.classLoader)
            answers = `in`.readArray(answers.javaClass.classLoader) as Array<Int?>
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeList(variants)
            out.writeArray(answers)
        }

        companion object {
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(`in`: Parcel): SavedState? {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        return SavedState(super.onSaveInstanceState()).apply {
            variants = this@ArrangeNumbersView.variants
            answers = this@ArrangeNumbersView.answers
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)

        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)

        variants = savedState.variants
        answers = savedState.answers

        updateAllTopChildren(answers)
        updateAllBottomChildren()
    }

    interface OnNewAnswerListener{
        fun onViewIsFilled(correctAnswersCount: Int)
        fun onTrueAnswer(correctAnswersCount: Int)
    }
}