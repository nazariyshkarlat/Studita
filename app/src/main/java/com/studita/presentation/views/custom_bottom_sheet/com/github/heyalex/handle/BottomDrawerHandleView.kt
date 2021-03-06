/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Alex Fialko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.studita.presentation.views.custom_bottom_sheet.com.github.heyalex.handle

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.studita.utils.dp
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.FloatRange
import androidx.core.content.ContextCompat
import com.studita.R
import com.studita.presentation.views.custom_bottom_sheet.com.github.heyalex.bottomdrawer.TranslationUpdater

class BottomDrawerHandleView : View, TranslationUpdater {

    @FloatRange(from = 0.0, to = 1.0)
    private var currentOffset = 0f
    private var rect = RectF()
    private var tempRect: RectF = RectF()

    private var paint = Paint()
    private var thickness = 6F.dp.toFloat()

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
        : super(context, attrs, defStyleAttr) {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            R.attr.disabled_background_color,
            typedValue,
            true
        )

        paint.apply {
            color = ContextCompat.getColor(context, typedValue.resourceId)
            strokeWidth = thickness
            flags = Paint.ANTI_ALIAS_FLAG
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(tempRect, thickness, thickness, paint)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        rect.set(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
    }

    override fun updateTranslation(@FloatRange(from = 0.0, to = 1.0) value: Float) {
        currentOffset = value
        val offset = (width.toFloat() * currentOffset) / 2
        tempRect.set(0 + offset, 0f, width - offset, height.toFloat())
        invalidate()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        superState?.let {
            val customViewSavedState = PlainHandleViewSavedState(superState)
            customViewSavedState.offset = currentOffset
            return customViewSavedState
        }
        return superState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val customViewSavedState = state as PlainHandleViewSavedState
        currentOffset = customViewSavedState.offset
        val offset = (width.toFloat() * currentOffset) / 2
        tempRect.set(0 + offset, 0f, width - offset, height.toFloat())
        super.onRestoreInstanceState(customViewSavedState.superState)
    }

    private class PlainHandleViewSavedState : BaseSavedState {

        internal var offset: Float = 0f

        constructor(superState: Parcelable) : super(superState)

        private constructor(source: Parcel) : super(source) {
            offset = source.readFloat()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeFloat(offset)
        }

        companion object CREATOR : Parcelable.Creator<PlainHandleViewSavedState> {
            override fun createFromParcel(source: Parcel): PlainHandleViewSavedState {
                return PlainHandleViewSavedState(source)
            }

            override fun newArray(size: Int): Array<PlainHandleViewSavedState?> {
                return arrayOfNulls(size)
            }
        }

        override fun describeContents(): Int {
            return 0
        }
    }
}