package com.studita.presentation.views.press_view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.studita.R

class PressImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr), IPressView {
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

        pressViewImpl.setPressAlpha(R.attr.press_view_press_alpha)
    }

    override fun setOnClickListener(work: (View) -> Unit) {
        isClickable = true
        pressViewImpl.onClick = work
    }

    fun setWithMinClickInterval(withMinClickInterval: Boolean) {
        pressViewImpl.withMinClickInterval = withMinClickInterval
    }
}