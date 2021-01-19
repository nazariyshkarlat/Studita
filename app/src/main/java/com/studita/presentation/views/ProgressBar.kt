package com.studita.presentation.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import androidx.annotation.FloatRange
import com.studita.R
import com.studita.presentation.animations.ProgressBarAnimation
import com.studita.utils.dp


class ProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {
    private var viewWidth = 0
    private var viewHeight = 0
    @FloatRange(from = 0.0, to = 1.0)
    var currentProgress = 0F
        set(value) {
            field = value
            invalidate()
        }
    private var strokeWidth: Int = 8F.dp
    private val animationDuration: Long = 400
    private var isCircleStyle = false
    private var roundedCorners = true
    private var progressColor = Color.BLACK
    private var progressBackgroundColor = Color.GRAY
    private val progressPaint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        if(attrs != null)
            initAttrs(context, attrs)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet){
        val attributes =
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.ProgressBar, 0, 0
            )

        strokeWidth = attributes.getDimension(
            R.styleable.ProgressBar_stroke_width,
            attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height")
                .toCharArray().filter { !it.isLetter() }.joinToString(
                    ""
                ).toFloat().dp.toFloat()
        ).toInt()


        isCircleStyle = attributes.getBoolean(
            R.styleable.ProgressBar_is_circle_style,
            false
        )

        progressColor = attributes.getColor(
            R.styleable.ProgressBar_progress_color,
            Color.BLACK
        )

        progressBackgroundColor = attributes.getColor(
            R.styleable.ProgressBar_progress_background_color,
            Color.GRAY
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        initMeasurments()

        if(isCircleStyle) {
            drawBackgroundArc(canvas)
            drawOutlineArc(canvas)
        }else{
            drawBackgroundRect(canvas)
            drawProgressRect(canvas)
        }
    }

    private fun initMeasurments() {
        viewWidth = width
        viewHeight = height
    }

    private fun drawOutlineArc(canvas: Canvas) {
        val diameter = viewWidth.coerceAtMost(viewHeight)
        val pad = (strokeWidth / 2.0).toFloat()
        val outerOval = RectF(pad, pad, diameter - pad, diameter - pad)
        progressPaint.color = progressColor
        progressPaint.strokeWidth = strokeWidth.toFloat()
        progressPaint.isAntiAlias = true
        progressPaint.strokeCap = if (roundedCorners) Paint.Cap.ROUND else Paint.Cap.BUTT
        progressPaint.style = Paint.Style.STROKE
        canvas.drawArc(
            outerOval,
            -90F,
            calcSweepAngleFromProgress(currentProgress),
            false,
            progressPaint
        )
    }

    private fun drawBackgroundArc(canvas: Canvas) {
        val diameter = viewWidth.coerceAtMost(viewHeight)
        val pad = (strokeWidth / 2.0).toFloat()
        val outerOval = RectF(pad, pad, diameter - pad, diameter - pad)
        progressPaint.color = progressBackgroundColor
        progressPaint.strokeWidth = strokeWidth.toFloat()
        progressPaint.isAntiAlias = true
        progressPaint.style = Paint.Style.STROKE
        canvas.drawArc(outerOval, 0f, 360f, false, progressPaint)
    }

    private fun drawProgressRect(canvas: Canvas) {
        progressPaint.color = progressColor
        progressPaint.isAntiAlias = true
        progressPaint.style = Paint.Style.FILL

        val margin = ((strokeWidth / 2) - (viewWidth * currentProgress)).coerceAtLeast(0F)

        canvas.drawRoundRect(
            RectF(
                0F,
                viewHeight - strokeWidth.toFloat() + margin,
                viewWidth * currentProgress - margin,
                viewHeight.toFloat()
            ), 999F, 999F, progressPaint
        )
    }

    private fun drawBackgroundRect(canvas: Canvas) {
        progressPaint.color = progressBackgroundColor
        progressPaint.isAntiAlias = true
        progressPaint.style = Paint.Style.FILL

        canvas.drawRoundRect(
            RectF(
                0F,
                viewHeight - strokeWidth.toFloat(),
                viewWidth.toFloat(),
                viewHeight.toFloat()
            ), 999F, 999F, progressPaint
        )
    }

    private fun calcSweepAngleFromProgress(progress: Float): Float {
        return 360F / 100 * progress * 100
    }

    private fun calcProgressFromSweepAngle(sweepAngle: Float): Int {
        return (sweepAngle * 100 / 360F).toInt()
    }

    /**
     * Set progress of the circular progress bar.
     * @param toPercent progress between 0.0 and 1.0.
     */
    fun animateProgress(
        toPercent: Float,
        onAnimEnd: () -> Unit = {},
        duration: Long = animationDuration,
        delay: Long = 0L,
        fromPercent: Float = currentProgress
    ) {
        val progressAnimation = ProgressBarAnimation(
            this,
            fromPercent,
            toPercent
        )
        progressAnimation.startOffset = delay
        progressAnimation.interpolator =
            androidx.interpolator.view.animation.FastOutSlowInInterpolator()
        progressAnimation.duration = duration
        progressAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                this@ProgressBar.clearAnimation()
                onAnimEnd.invoke()
            }

            override fun onAnimationStart(animation: Animation?) {}
        })
        this.startAnimation(progressAnimation)
    }

    fun setProgressColor(color: Int) {
        progressColor = color
        invalidate()
    }

    fun setProgressWidth(width: Int) {
        strokeWidth = width
        invalidate()
    }

    /**
     * Toggle this if you don't want rounded corners on progress bar.
     * Default is true.
     * @param roundedCorners true if you want rounded corners of false otherwise.
     */
    fun useRoundedCorners(roundedCorners: Boolean) {
        this.roundedCorners = roundedCorners
        invalidate()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable("superState", super.onSaveInstanceState())
        bundle.putFloat("currentProgress", this.currentProgress)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            this.currentProgress = state.getFloat("currentProgress")
            super.onRestoreInstanceState(state.getParcelable("superState"))
        }else
            super.onRestoreInstanceState(state)
    }
}