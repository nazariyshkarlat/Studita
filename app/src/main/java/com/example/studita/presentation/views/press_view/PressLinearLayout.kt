package com.example.studita.presentation.views.press_view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.example.studita.R


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

        pressViewImpl = PressView(this, withMinClickInterval)

        pressViewImpl.setPressAlpha(R.attr.press_view_press_alpha_lighter)
    }

    fun setWithMinClickInterval(withMinClickInterval: Boolean) {
        pressViewImpl.withMinClickInterval = withMinClickInterval
    }

    override fun setOnClickListener(work: (View) -> Unit) {
        pressViewImpl.onClick = work
    }
}