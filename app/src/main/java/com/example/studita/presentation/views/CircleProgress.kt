package com.example.myapplication.ui.custom_views

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.example.studita.R
import kotlin.math.acos
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.LinearLayout

class CircleProgress @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val rectF = RectF()

    var pause = false
    var topBottomPadding = 0F
    var rightLeftPadding = 0F
    var dotSize = 0F
    var timerDuration = 0F
    lateinit var animatorListener: Animator.AnimatorListener
    lateinit var animator: ValueAnimator
    var onEndAnim : () -> Unit = {
    }
    var progress = 0
        set(progress) {
            field = progress
            if (this.progress > max) {
                field %= max
            }
            invalidate()
        }
    var max: Int = 0
        set(max) {
            if (max > 0) {
                field = max
                invalidate()
            }
        }
    var finishedColor: Int = 0
        set(value){
            field = value
            this.invalidate()
        }
    var unfinishedColor: Int = 0
        set(value){
            field = value
            this.invalidate()
        }

    private val defaultFinishedColor = Color.rgb(66, 145, 241)
    private val defaultUnfinishedColor = Color.rgb(204, 204, 204)
    private val defaultTimerDuration = 4000F
    private val paint = Paint()

    init {

        val attributes =
            context.theme.obtainStyledAttributes(attrs,
                R.styleable.CircleProgress, defStyleAttr, 0)
        initByAttributes(attributes)
        attributes.recycle()

        initPainters()
    }

    private fun initByAttributes(attributes: TypedArray) {
        finishedColor = attributes.getColor(
            R.styleable.CircleProgress_circle_finished_color,
            defaultFinishedColor
        )
        unfinishedColor = attributes.getColor(
            R.styleable.CircleProgress_circle_unfinished_color,
            defaultUnfinishedColor
        )

        timerDuration = attributes.getFloat(
            R.styleable.CircleProgress_timerDuration,
            defaultTimerDuration
        )

        val vto = viewTreeObserver
        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                    this@CircleProgress.viewTreeObserver.removeOnGlobalLayoutListener(this)
                dotSize = measuredHeight.toFloat()
                this@CircleProgress.layoutParams = LinearLayout.LayoutParams(
                    (dotSize + (rightLeftPadding*2)).toInt(),
                    (dotSize + (topBottomPadding*2)).toInt()
                )
            }
        })

        rightLeftPadding = attributes.getDimension(
            R.styleable.CircleProgress_rightLeftPadding,
            0F
        )

        topBottomPadding = attributes.getDimension(
            R.styleable.CircleProgress_topBottomPadding,
            0F
        )

        max = 100
        progress = attributes.getInt(R.styleable.CircleProgress_circle_progress, 0)
    }

    private fun initPainters() {
        paint.isAntiAlias = true
    }

    override fun invalidate() {
        initPainters()
        super.invalidate()
    }

    fun animateProgress(progress: Int = 100, animateAlpha: Boolean = true) {
        this.progress = progress
        if(animateAlpha) {
            alpha = 0.08F
        }
        animator = ValueAnimator.ofInt(progress, 0)
        animator.interpolator = LinearInterpolator()
        animator.duration = (progress/max.toFloat() * timerDuration).toLong()
        animator.addUpdateListener{ animation -> this.progress = animation.animatedValue as Int }
        animatorListener = object: Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                onEndAnim()
                animator.removeListener(this)
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        }
        animator.addListener(animatorListener)
        if(animateAlpha) {
            val alphaAnim = animate().alpha(1F).setDuration(300)
            alphaAnim.setListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    animator.start()
                    alphaAnim.setListener(null)
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }

            }).start()
        }else
            animator.start()
        pause = false
    }

    fun stopAnimation(){
        animator.removeListener(animatorListener)
        animator.duration = 0
    }

    fun pause(value: Long = animator.currentPlayTime){
        animator.removeListener(animatorListener)
        animator.currentPlayTime = value
        pause = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        rectF.set(
            rightLeftPadding,
            topBottomPadding,
            MeasureSpec.getSize(widthMeasureSpec).toFloat() - rightLeftPadding,
            MeasureSpec.getSize(heightMeasureSpec).toFloat() - topBottomPadding
        )
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        val yHeight = progress / max.toFloat() * dotSize
        val radius = dotSize / 2f
        val angle = (acos(((radius - yHeight) / radius).toDouble()) * 180 / Math.PI).toFloat()
        val startAngle = 90 + angle
        val sweepAngle = 360 - angle * 2
        paint.color = unfinishedColor
        canvas.drawArc(rectF, startAngle, sweepAngle, false, paint)

        canvas.save()
        canvas.rotate(180f, width / 2.toFloat(), height / 2.toFloat())
        paint.color = finishedColor
        canvas.drawArc(rectF, 270 - angle, angle * 2, false, paint)
        canvas.restore()
    }
}