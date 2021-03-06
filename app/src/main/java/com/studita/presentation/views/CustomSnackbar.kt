package com.studita.presentation.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.GradientDrawable
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.contains
import com.studita.R
import com.studita.presentation.draw.draw
import com.studita.presentation.draw.getMultilineTextLayout
import com.studita.utils.dp
import com.studita.utils.getAlphaAnimator
import com.studita.utils.getScaleAnimator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean


class CustomSnackbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object{

        @Volatile
        private var isHiding: AtomicBoolean = AtomicBoolean(false)

        fun hide(parent: ViewGroup?){
            if(!isHiding.get()) {
                val snackbar = parent?.findViewById<View?>(R.id.customSnackbar)
                snackbar
                    ?.animate()
                    ?.alpha(0F)
                    ?.start()

                isHiding.set(true)
            }
        }
    }

    private val rectF = RectF()
    private var desiredHeight = 0
    private val radius = 8F.dp.toFloat()
    private val innerPadding = 16F.dp
    private val margin = 8F.dp
    private var bottomMarginExtra = 0

    private lateinit var textLayout: StaticLayout
    private var textY = 0F
    private var textHeight = 0
    private var text: String = ""
    private var textPaint = TextPaint().apply {
        textSize = 16F.dp.toFloat()
        textAlign = Paint.Align.LEFT
        isAntiAlias = true
        typeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
    }

    private val ANIMATION_SCALE_FROM_VALUE = 0.8f
    private val ANIMATION_FADE_IN_DURATION = 150

    private var params: FrameLayout.LayoutParams? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textLayout.draw(canvas, innerPadding.toFloat(), textY)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)

        textLayout = getMultilineTextLayout(text, textPaint, width - innerPadding * 2)

        textHeight = textLayout.height
        desiredHeight = textHeight + innerPadding * 2

        rectF.set(0F, 0F, width.toFloat(), desiredHeight.toFloat())

        textY = rectF.height() / 2f + textHeight / 2f - textHeight

        setMeasuredDimension(width, desiredHeight)
    }

    fun show(
        text: String,
        @ColorInt color: Int,
        @ColorInt textColor: Int,
        duration: Long = 3000L,
        delay: Long = 0L,
        bottomMarginExtra: Int = 0,
        contentView: ViewGroup? = null,
    ) {
        this.bottomMarginExtra = bottomMarginExtra
        val rootView = contentView
            ?: (context as Activity).window.decorView.rootView.findViewById(android.R.id.content) as ViewGroup
        val oldSnackbar: CustomSnackbar? = rootView.findViewById(R.id.customSnackbar)
        oldSnackbar?.let {
            rootView.removeView(it)
        }
        if (!rootView.contains(this)) {
            initView(text, color, textColor)
            this.id = R.id.customSnackbar
            rootView.addView(this)
            startAnimation(duration, delay = delay) {
                rootView.removeView(this)
            }
            isHiding.set(false)
        }
    }

    private fun initView(text: String, @ColorInt color: Int, @ColorInt textColor: Int) {
        this.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = radius
            setColor(color)
        }
        textPaint.apply {
            this.color = textColor
        }
        this.text = text
        setViewParams()
        this.elevation = resources.getDimension(R.dimen.snackbarElevation)
        this.layoutParams = params
    }

    private fun startAnimation(duration: Long, delay: Long, onAnimationEnd: () -> Unit) {
        alpha = 0F
        val alphaAnimator: ValueAnimator? = getAlphaAnimator(0f, 1f)
        val scaleAnimator: ValueAnimator? =
            getScaleAnimator(ANIMATION_SCALE_FROM_VALUE, 1f)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(alphaAnimator, scaleAnimator)
        animatorSet.duration = ANIMATION_FADE_IN_DURATION.toLong()
        animatorSet.startDelay = delay
        animatorSet.start()
        startAnimationCoroutine(duration, onAnimationEnd)
    }

    private fun View.startAnimationCoroutine(duration: Long, onAnimationEnd: () -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            delay(duration)
            this@startAnimationCoroutine.animate().alpha(0F)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        onAnimationEnd.invoke()
                    }
                }).start()
            isHiding.set(true)
        }
    }

    private fun setViewParams() {
        params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(margin, margin, margin, margin + bottomMarginExtra)
            gravity = Gravity.BOTTOM
        }
    }

}