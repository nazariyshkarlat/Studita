package com.studita.presentation.fragments.bottom_sheets

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.studita.R
import com.studita.presentation.fragments.bottom_sheets.PromoBottomSheetFragment.Companion.formCloseButton
import com.studita.presentation.views.PromoFragmentScreenView
import com.studita.presentation.views.custom_bottom_sheet.CustomBottomSheetBehavior
import com.studita.presentation.views.custom_bottom_sheet.com.github.heyalex.bottomdrawer.BottomDrawerDialog
import com.studita.presentation.views.custom_bottom_sheet.com.github.heyalex.bottomdrawer.BottomDrawerFragment
import com.studita.utils.ThemeUtils
import com.studita.utils.clearLightStatusBar
import com.studita.utils.dpToPx

class PromoBottomSheetEndFragment : BottomDrawerFragment(){

    private var isExpanded = false
    private var slideOffset = 0F

    override fun configureBottomDrawer(): BottomDrawerDialog {
        return BottomDrawerDialog.build(context!!) {
            isWrapContent = false
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

            override fun onCloseButtonClick() {
                activity?.finish()
            }

        }.apply {
            formScreenView(
                -1,
                null,
                R.string.promo_end_screen_title,
                R.string.promo_end_screen_subtitle,
                isEndBottomSheet = true
            )

            formBottomSheetPeeckHeader(R.string.promo_end_screen_title, true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addBottomSheetCallback {
            onSlide { _, slideOffset ->

                if(slideOffset == 1F && isExpanded)
                    bottomDrawerDialog?.behavior?.isDraggable = false


                view.findViewById<ViewGroup>(R.id.promoBottomSheetPeeckView).alpha = 1F - slideOffset * 2F

                this@PromoBottomSheetEndFragment.slideOffset = slideOffset
            }
        }

        setupBackPressListener()
    }


    override fun onStart() {
        super.onStart()

        dialog?.window?.decorView?.clearLightStatusBar()

        bottomDrawerDialog?.drawer?.offsetTrigger = 0.9F
        formCloseButton(bottomDrawerDialog!!)
        bottomDrawerDialog?.drawer?.changeBackgroundColor(ThemeUtils.getAccentColor(context!!))

        bottomDrawerDialog?.behavior?.apply {

            if(state != CustomBottomSheetBehavior.STATE_HIDDEN)
                isHideable = false


            if(state == CustomBottomSheetBehavior.STATE_EXPANDED) {
                bottomDrawerDialog?.behavior?.isDraggable = false
            }

            bottomDrawerDialog?.behavior?.isDraggable = state == CustomBottomSheetBehavior.STATE_COLLAPSED

            addBottomSheetCallback(object : CustomBottomSheetBehavior.BottomSheetCallback(){
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        CustomBottomSheetBehavior.STATE_EXPANDED -> {
                            isExpanded = true

                            if(slideOffset == 1F && isExpanded) {
                                bottomDrawerDialog?.behavior?.isDraggable = false
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