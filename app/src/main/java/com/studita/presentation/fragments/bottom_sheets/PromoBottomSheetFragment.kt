package com.studita.presentation.fragments.bottom_sheets

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.studita.R
import com.studita.presentation.fragments.bottom_sheets.PromoBottomSheetFragment.Companion.formCloseButton
import com.studita.presentation.views.PromoFragmentScreenView
import com.studita.presentation.views.custom_bottom_sheet.CustomBottomSheetBehavior
import com.studita.presentation.views.custom_bottom_sheet.com.github.heyalex.bottomdrawer.BottomDrawerDialog
import com.studita.presentation.views.custom_bottom_sheet.com.github.heyalex.bottomdrawer.BottomDrawerFragment
import com.studita.utils.ColorUtils
import com.studita.utils.ThemeUtils
import com.studita.utils.ThemeUtils.getCurrentTheme
import com.studita.utils.clearLightStatusBar
import com.studita.utils.dpToPx

class PromoBottomSheetFragment : BottomDrawerFragment(){

    private var isExpanded = false
    private var callbackWasSent = false
    private var slideOffset = 0F

    companion object {

        private const val IMAGE_KEY = "PROMO_BOTTOM_SHEET_FRAGMENT_IMAGE"
        private const val TITLE_KEY = "PROMO_BOTTOM_SHEET_FRAGMENT_TITLE"
        private const val SUBTITLE_KEY = "PROMO_BOTTOM_SHEET_FRAGMENT_SUBTITLE"
        private const val SCREEN_NUMBER_KEY = "PROMO_BOTTOM_SHEET_FRAGMENT_SCREEN_NUMBER"

        fun getInstance(screenNumber: Int, @DrawableRes image: Int, @StringRes title: Int, @StringRes subtitle: Int) : PromoBottomSheetFragment {
            return PromoBottomSheetFragment().apply {
                arguments = bundleOf(
                    IMAGE_KEY to image,
                    SCREEN_NUMBER_KEY to screenNumber,
                    TITLE_KEY to title,
                    SUBTITLE_KEY to subtitle)
            }
        }
        
        fun BottomDrawerFragment.formCloseButton(bottomDrawerDialog: BottomDrawerDialog){
            (bottomDrawerDialog.drawer?.parent as ViewGroup?)?.findViewById<CoordinatorLayout>(R.id.bottom_sheet_coordinator)?.addView(
                ImageView(context!!).apply {
                    id = R.id.promoEndBottomSheetCloseButton
                    layoutParams = CoordinatorLayout.LayoutParams(
                        CoordinatorLayout.LayoutParams.WRAP_CONTENT,
                        CoordinatorLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.TOP or Gravity.START
                        topMargin = resources.getDimension(R.dimen.statusBarHeight).toInt()
                    }

                    setBackgroundResource(R.drawable.circle_background_16dp)
                    setPadding(16F.dpToPx(),16F.dpToPx(), 16F.dpToPx(), 16F.dpToPx())
                    setImageResource(R.drawable.ic_close_white)

                    if(this@formCloseButton.context!!.getCurrentTheme() == ThemeUtils.Theme.LIGHT && arguments?.getInt(SCREEN_NUMBER_KEY) == 1)
                        drawable?.setTint(ThemeUtils.getPrimaryColor(context))
                    else
                        drawable?.setTint(ContextCompat.getColor(context, R.color.white))

                    setOnClickListener {
                        activity?.finish()
                    }
                }
            )
        }
    }

    override fun configureBottomDrawer(): BottomDrawerDialog {
        return BottomDrawerDialog.build(context!!) {
            isWrapContent = true
            isPeekView = true
            isCancelableOnTouchOutside = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return object : PromoFragmentScreenView(context!!){
            override fun onShowLessButtonClick() {
                expandWithBehaivor()
            }

            override fun onCloseButtonClick() {}

        }.apply {
            formScreenView(
                arguments!!.getInt(SCREEN_NUMBER_KEY),
                arguments!!.getInt(IMAGE_KEY),
                arguments!!.getInt(TITLE_KEY),
                arguments!!.getInt(SUBTITLE_KEY)
            )

            formBottomSheetPeeckHeader(arguments!!.getInt(TITLE_KEY))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addBottomSheetCallback {
            onSlide { _, slideOffset ->

                view.findViewById<ViewGroup>(R.id.promoBottomSheetPeeckView).alpha = 1F - (slideOffset * 2F)

                if(slideOffset == 1F && isExpanded && !callbackWasSent){
                    callbackWasSent = true
                    (parentFragment as? BottomSheetExpandCallback)?.onExpand()
                    bottomDrawerDialog?.behavior?.isDraggable = false
                }

                view.background?.alpha =((1F - (slideOffset * 2F)).coerceAtLeast(0F)*255).toInt()

                if(context?.getCurrentTheme() == ThemeUtils.Theme.LIGHT && arguments!!.getInt(SCREEN_NUMBER_KEY) == 1) {
                    (bottomDrawerDialog?.drawer?.parent as ViewGroup?)
                        ?.findViewById<CoordinatorLayout>(R.id.bottom_sheet_coordinator)
                        ?.findViewById<ImageView>(R.id.promoEndBottomSheetCloseButton)?.drawable?.setTint(ColorUtils.compositeColors(ThemeUtils.getPrimaryColor(context!!), ContextCompat.getColor(context!!, R.color.white), slideOffset.coerceAtLeast(0F)))
                }

                this@PromoBottomSheetFragment.slideOffset = slideOffset
            }
        }

        setupBackPressListener()
    }

    override fun onStart() {
        super.onStart()

        formCloseButton(bottomDrawerDialog!!)
        bottomDrawerDialog?.drawer?.offsetTrigger = 0.9F
        bottomDrawerDialog?.behavior?.apply {

            if(state !=CustomBottomSheetBehavior.STATE_HIDDEN)
                isHideable = false

            if(state ==CustomBottomSheetBehavior.STATE_EXPANDED) {
                bottomDrawerDialog?.behavior?.isDraggable = false
            }

            bottomDrawerDialog?.behavior?.isDraggable = state == CustomBottomSheetBehavior.STATE_COLLAPSED


            if(arguments!!.getInt(SCREEN_NUMBER_KEY) != 1)
                dialog?.window?.decorView?.clearLightStatusBar()
            else if(state ==CustomBottomSheetBehavior.STATE_EXPANDED)
                dialog?.window?.decorView?.clearLightStatusBar()

            addBottomSheetCallback(object :CustomBottomSheetBehavior.BottomSheetCallback(){
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                       CustomBottomSheetBehavior.STATE_EXPANDED -> {
                            isExpanded = true

                            if(slideOffset == 1F && isExpanded && !callbackWasSent){
                                callbackWasSent = true
                                (parentFragment as? BottomSheetExpandCallback)?.onExpand()
                                bottomDrawerDialog?.behavior?.isDraggable = false
                                dialog?.window?.decorView?.clearLightStatusBar()
                            }
                        }
                       CustomBottomSheetBehavior.STATE_COLLAPSED -> {
                            bottomDrawerDialog?.behavior?.isHideable = false
                           bottomDrawerDialog?.behavior?.isDraggable = true
                       }
                        else -> {}
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                }

            })
            peekHeight = 48F.dpToPx()
        }
    }


    interface BottomSheetExpandCallback{

        fun onExpand()

    }

    private fun setupBackPressListener() {
        this.view?.isFocusableInTouchMode = true
        this.view?.requestFocus()
        this.view?.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                activity?.finish()
                true
            } else
                false
        }
    }

}