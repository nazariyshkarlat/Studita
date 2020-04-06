package com.example.studita.presentation.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.example.studita.R
import com.example.studita.presentation.listeners.ViewOnTouch
import com.example.studita.presentation.utils.dpToPx
import kotlin.math.ceil


class CustomRatingBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr){

    private var selectedStar = 0

    private val starsCount = 5

    private val starHeight = 20.dpToPx()
    private val starWidth = 20.dpToPx()
    private val starMargin = 2.dpToPx()

    private val viewHeight = starHeight
    private val viewWidth = starWidth*starsCount + (starMargin*2)*(starsCount-1)

    private var onRatingSelected: (Int) -> Unit  = {}

    private val starEmpty: VectorDrawableCompat =
        VectorDrawableCompat.create(context.resources, R.drawable.ic_star_border_50, context.theme)!!.apply {
            setBounds(0, 0, starHeight, starWidth)
            println(starHeight)
        }
    private val starFilled: VectorDrawableCompat  =
        VectorDrawableCompat.create(context.resources, R.drawable.ic_star_green, context.theme)!!.apply {
            setBounds(0, 0, starHeight, starWidth)
        }

    init {
        val a: TypedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomRatingBar,
            0, 0);

        selectedStar = a.getInteger(R.styleable.CustomRatingBar_selectedStar, 0)

        if(isClickable)
            setOnTouch()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for(i in 1 ..  starsCount) {
            canvas.translate(starMargin.toFloat(), 0F)
            if(i > selectedStar)
                starEmpty.draw(canvas)
            else
                starFilled.draw(canvas)
            canvas.translate(starWidth+starMargin.toFloat(), 0F)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width: Int
        val height: Int

        //Measure Width
        width = when (widthMode) {
            MeasureSpec.EXACTLY -> { //Must be this size
                widthSize
            }
            MeasureSpec.AT_MOST -> { //Can't be bigger than...
                viewWidth.coerceAtMost(widthSize)
            }
            else -> { //Be whatever you want
                viewWidth
            }
        }

        //Measure Height
        height = when (heightMode) {
            MeasureSpec.EXACTLY -> { //Must be this size
                heightSize
            }
            MeasureSpec.AT_MOST -> { //Can't be bigger than...
                viewHeight.coerceAtMost(heightSize)
            }
            else -> { //Be whatever you want
                viewHeight
            }
        }

        //MUST CALL THIS
        //MUST CALL THIS
        setMeasuredDimension(width, height)
        setMeasuredDimension(viewWidth, viewHeight)
    }

    private fun getStarNumberByX(x: Float): Int {
        return ceil(x / (starMargin * 2 + starWidth.toDouble())).toInt()
    }

    private fun setOnTouch(){
        setOnTouchListener(object: ViewOnTouch() {
            override fun onDownTouchAction(x: Float, y: Float) {
                val selectedStar = getStarNumberByX(x)
                if (this@CustomRatingBar.selectedStar != selectedStar) {
                    this@CustomRatingBar.selectedStar = selectedStar
                    invalidate()
                }
            }

            override fun onUpTouchAction(x: Float, y: Float) {
                if (selectedStar != 0)
                    onRatingSelected.invoke(selectedStar)
            }

            override fun onCancelTouchAction(x: Float, y: Float) {
                this@CustomRatingBar.selectedStar = 0
                invalidate()
            }

            override fun onMoveTouchAction(x: Float, y: Float) {
                if (selectedStar != getStarNumberByX(x)) {
                    this@CustomRatingBar.selectedStar = 0
                    invalidate()
                }
            }

        })
    }

    fun setOnRatingSelectedListener(work: (Int) -> Unit){
        onRatingSelected = work
    }

}