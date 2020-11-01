package com.example.studita.presentation.views.custom_bottom_sheet.com.github.heyalex.bottomdrawer

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDialog
import androidx.core.content.ContextCompat
import com.example.studita.R
import com.example.studita.presentation.views.custom_bottom_sheet.CustomBottomSheetBehavior
import com.example.studita.presentation.views.custom_bottom_sheet.com.github.heyalex.utils.BottomDialog
import com.example.studita.presentation.views.custom_bottom_sheet.com.github.heyalex.utils.BottomDrawerDelegate
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.ThemeUtils
import com.example.studita.utils.ThemeUtils.getCurrentTheme

open class BottomDrawerDialog(context: Context, @StyleRes theme: Int = R.style.BottomDialogTheme) :
    AppCompatDialog(context, theme), BottomDialog {

    internal var bottomDrawerDelegate = BottomDrawerDelegate(this.context, this)
    internal val drawer: BottomDrawer?
        get() = bottomDrawerDelegate.drawer

    internal val behavior: CustomBottomSheetBehavior<BottomDrawer>?
        get() = bottomDrawerDelegate.behavior

    init {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.let {
            it.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            it.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            it.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            it.statusBarColor = Color.TRANSPARENT

            if(context.getCurrentTheme() == ThemeUtils.Theme.LIGHT) {
                var flags = it.decorView.systemUiVisibility
                flags =
                    flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    flags =
                        flags xor View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                }
                it.decorView.systemUiVisibility = flags
            }
        }
    }


    override fun setContentView(@LayoutRes layoutResId: Int) {
        super.setContentView(bottomDrawerDelegate.wrapInBottomSheet(layoutResId, null, null))
    }

    override fun setContentView(view: View) {
        super.setContentView(bottomDrawerDelegate.wrapInBottomSheet(0, view, null))
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams?) {
        super.setContentView(bottomDrawerDelegate.wrapInBottomSheet(0, view, params))
    }


    override fun onStart() {
        super.onStart()
        bottomDrawerDelegate.open()
    }

    override fun onBackPressed() {
        bottomDrawerDelegate.onBackPressed()
    }

    override fun onSaveInstanceState(): Bundle {
        val superState = super.onSaveInstanceState()
        bottomDrawerDelegate.onSaveInstanceState(superState)
        return superState
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        bottomDrawerDelegate.onRestoreInstanceState(savedInstanceState)
    }

    override fun onDismiss() {
        dismiss()
    }

    override fun onCancel() {
        cancel()
    }

    companion object {
        inline fun build(context: Context, block: Builder.() -> Unit) =
            Builder(context).apply(block).build()
    }

    class Builder(
        val context: Context
    ) {
        var theme: Int = R.style.BottomDialogTheme
        var handleView: View? = null
        var isWrapContent = false
        var isPeekView = false
        var isCancelableOnTouchOutside: Boolean? = null

        fun build() = BottomDrawerDialog(context, theme).apply {
            whenNotNull(this@Builder.isCancelableOnTouchOutside) {
                bottomDrawerDelegate.isCancelableOnTouchOutside = it
            }

            whenNotNull(this@Builder.handleView) {
                bottomDrawerDelegate.handleView = it
            }

            bottomDrawerDelegate.isPeekView = isPeekView
            bottomDrawerDelegate.isWrapContent = isWrapContent
        }

        private inline fun <T : Any, R> whenNotNull(input: T?, callback: (T) -> R): R? {
            return input?.let(callback)
        }
    }
}