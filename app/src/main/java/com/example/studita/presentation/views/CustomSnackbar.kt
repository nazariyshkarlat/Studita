package com.example.studita.presentation.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.FrameLayout
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.contains
import com.example.studita.R
import com.example.studita.presentation.draw.draw
import com.example.studita.presentation.draw.getMultilineTextLayout
import com.example.studita.presentation.utils.dpToPx
import com.google.android.material.animation.AnimationUtils
import com.google.android.material.snackbar.BaseTransientBottomBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class CustomSnackbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var backgroundPaint = Paint()
    private val rectF = RectF()
    private var desiredHeight = 0
    private val radius = 8.dpToPx().toFloat()
    private val innerPadding = 16.dpToPx()
    private val margin = 16.dpToPx()

    private lateinit var textLayout: StaticLayout
    private var textY = 0F
    private var textHeight = 0
    private var text: String = ""
    private var textPaint = TextPaint().apply {
        color = ContextCompat.getColor(context, R.color.white)
        textSize = 16.dpToPx().toFloat()
        textAlign = Paint.Align.LEFT
        isAntiAlias = true
        typeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
    }

    private val ANIMATION_SCALE_FROM_VALUE = 0.8f
    private val ANIMATION_FADE_IN_DURATION = 150

    private val params =  FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
        setMargins(margin, margin, margin, margin)
        gravity = Gravity.BOTTOM
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRoundRect(rectF, radius, radius, backgroundPaint)
        textLayout.draw(canvas, innerPadding.toFloat(), textY)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)

        textLayout = getMultilineTextLayout(text, textPaint, width - innerPadding*2)

        textHeight = textLayout.height
        desiredHeight = textHeight + innerPadding*2

        rectF.set(0F,0F, width.toFloat(), desiredHeight.toFloat())

        textY = rectF.height()/2f + textHeight / 2f - textHeight

        setMeasuredDimension(width, desiredHeight)
    }

    fun show (text: String,@ColorRes colorId: Int, duration: Long = 3000L){
        val rootView = ((context as Activity).window.decorView.rootView.findViewById(android.R.id.content) as ViewGroup)
        if(!rootView.contains(this)) {
            initView(text, colorId)
            rootView.addView(this)
            setAnimation(duration){
                rootView.removeView(this@CustomSnackbar)
            }
        }
    }

    private fun initView(text: String,@ColorRes colorId: Int){
        backgroundPaint.color = ContextCompat.getColor(context, colorId)
        this.text = text
        this.layoutParams = params
    }

    private fun setAnimation(duration: Long, onAnimationEnd: () -> Unit) {
        val alphaAnimator: ValueAnimator? = getAlphaAnimator(0f, 1f)
        val scaleAnimator: ValueAnimator? =
            getScaleAnimator(ANIMATION_SCALE_FROM_VALUE, 1f)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(alphaAnimator, scaleAnimator)
        animatorSet.duration = ANIMATION_FADE_IN_DURATION.toLong()
        animatorSet.addListener(object  : AnimatorListenerAdapter(){})
        GlobalScope.launch(Dispatchers.Main) {
            animatorSet.start()
            delay(duration)
            this@CustomSnackbar.animate().alpha(0F).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    onAnimationEnd.invoke()
                }
            }).start()
        }
    }


    private fun getAlphaAnimator(vararg alphaValues: Float): ValueAnimator? {
        val animator = ValueAnimator.ofFloat(*alphaValues)
        animator.interpolator = AnimationUtils.LINEAR_INTERPOLATOR
        animator.addUpdateListener { valueAnimator -> this.alpha = valueAnimator.animatedValue as Float }
        return animator
    }

    private fun getScaleAnimator(vararg scaleValues: Float): ValueAnimator? {
        val animator = ValueAnimator.ofFloat(*scaleValues)
        animator.interpolator = AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR
        animator.addUpdateListener { valueAnimator ->
            val scale = valueAnimator.animatedValue as Float
            scaleX = scale
            scaleY = scale
        }
        return animator
    }

}