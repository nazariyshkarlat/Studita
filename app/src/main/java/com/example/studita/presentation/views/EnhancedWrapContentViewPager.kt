package com.example.studita.presentation.views

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.children
import androidx.viewpager.widget.ViewPager

class EnhancedWrapContentViewPager @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : ViewPager(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightSpec = heightMeasureSpec
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        // Unspecified means that the ViewPager is in a ScrollView WRAP_CONTENT.
// At Most means that the ViewPager is not in a ScrollView WRAP_CONTENT.
        if (mode == MeasureSpec.UNSPECIFIED || mode == MeasureSpec.AT_MOST) { // super has to be called in the beginning so the child views can be initialized.
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            var height = 0
            children.forEach{child ->
                child.measure(
                    widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                )
                val childMeasuredHeight = child.measuredHeight
                if (childMeasuredHeight > height) {
                    height = childMeasuredHeight
                }
            }
            heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        }
        // super has to be called again so the new specs are treated as exact measurements
        super.onMeasure(widthMeasureSpec, heightSpec)
    }
}