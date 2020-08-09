package com.example.studita.presentation.views

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.example.studita.utils.ScreenUtils


class PrivacyDuelsExceptionsMaxHeightRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(
            widthSpec,
            MeasureSpec.makeMeasureSpec(
                (ScreenUtils.getScreenHeight() * 0.5).toInt(),
                MeasureSpec.AT_MOST
            )
        )
    }
}