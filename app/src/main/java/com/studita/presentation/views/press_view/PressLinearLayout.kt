package com.studita.presentation.views.press_view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.studita.R


class PressLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), IPressView {

    private val pressViewImpl: PressView

    init {
        val a: TypedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PressView,
            0, 0
        );

        val withMinClickInterval =
            a.getBoolean(R.styleable.PressView_with_min_click_interval, false)

        val pressAlpha =
            a.getFloat(R.styleable.PressView_press_alpha, -1F)

        pressViewImpl = PressView(this, withMinClickInterval)

        if(pressAlpha == -1F)
            pressViewImpl.setPressAlpha(R.attr.press_view_press_alpha)
        else
            pressViewImpl.pressAlpha =  pressAlpha
    }

    fun setWithMinClickInterval(withMinClickInterval: Boolean) {
        pressViewImpl.withMinClickInterval = withMinClickInterval
    }

    override fun setOnClickListener(work: (View) -> Unit) {
        isClickable = true
        pressViewImpl.onClick = work
    }
}