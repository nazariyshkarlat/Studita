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