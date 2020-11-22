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

package com.studita.presentation.views.custom_bottom_sheet.com.github.heyalex.bottomdrawer

import android.app.Dialog
import android.os.Bundle
import android.view.ViewTreeObserver
import com.studita.presentation.views.custom_bottom_sheet.CustomBottomSheetBehavior
import com.studita.presentation.views.custom_bottom_sheet.com.github.heyalex.utils.BottomDrawerDelegate

abstract class BottomDrawerFragment : androidx.fragment.app.DialogFragment(), ViewTreeObserver.OnGlobalLayoutListener {

    protected var bottomDrawerDialog: BottomDrawerDialog? = null
    private var lastHeight = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = configureBottomDrawer()
        bottomDrawerDialog = dialog
        return dialog
    }

    open fun configureBottomDrawer() : BottomDrawerDialog {
        return BottomDrawerDialog(context!!)
    }

    override fun onStart() {
        super.onStart()
        bottomDrawerDialog?.drawer?.viewTreeObserver?.addOnGlobalLayoutListener(this)
        dialog?.setOnDismissListener {
            if (isAdded) {
                dismissAllowingStateLoss()
            }
        }
    }

    fun dismissWithBehavior() {
        bottomDrawerDialog?.behavior?.state = CustomBottomSheetBehavior.STATE_HIDDEN
    }

    fun expandWithBehaivor() {
        bottomDrawerDialog?.behavior?.state = CustomBottomSheetBehavior.STATE_EXPANDED
    }

    fun getCurrentState(): Int? {
        return bottomDrawerDialog?.behavior?.state
    }

    override fun onGlobalLayout() {
        if(this.view?.measuredHeight != lastHeight)
            bottomDrawerDialog?.drawer?.globalTranslationViews()
        lastHeight = this.view?.measuredHeight ?: 0
    }

    fun addBottomSheetCallback(func: BottomDrawerDelegate.BottomSheetCallback.() -> Unit): CustomBottomSheetBehavior.BottomSheetCallback? {
        return bottomDrawerDialog?.bottomDrawerDelegate?.addBottomSheetCallback(func)
    }

    fun removeBottomSheetCallback(callback: CustomBottomSheetBehavior.BottomSheetCallback) {
        bottomDrawerDialog?.bottomDrawerDelegate?.removeBottomSheetCallback(callback)
    }

    fun changeBackgroundColor(color: Int) {
        bottomDrawerDialog?.drawer?.changeBackgroundColor(color)
    }

    fun changeExtraPadding(extraPadding: Int) {
        bottomDrawerDialog?.drawer?.changeExtraPadding(extraPadding)
    }
}