package com.example.studita.presentation.fragments.bottom_sheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.view_model.ExerciseReportBugBottomSheetFragmentViewModel
import com.example.studita.presentation.view_model.OfflineModeDownloadFragmentViewModel
import com.example.studita.presentation.views.custom_bottom_sheet.CustomBottomSheetBehavior
import com.example.studita.presentation.views.custom_bottom_sheet.CustomBottomSheetBehavior.STATE_COLLAPSED
import com.example.studita.presentation.views.custom_bottom_sheet.com.github.heyalex.bottomdrawer.BottomDrawerDialog
import com.example.studita.presentation.views.custom_bottom_sheet.com.github.heyalex.bottomdrawer.BottomDrawerFragment
import kotlinx.android.synthetic.main.offline_mode_download_error_bottom_sheet_layout.*

class OfflineModeDownloadErrorBottomSheet : BottomDrawerFragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.offline_mode_download_error_bottom_sheet_layout, container, false)
    }

    override fun configureBottomDrawer(): BottomDrawerDialog {
        return BottomDrawerDialog.build(context!!) {
            isWrapContent = true
            isCancelableOnTouchOutside = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProviders.of(parentFragment!!).get(OfflineModeDownloadFragmentViewModel::class.java)

        offlineModeDownloadErrorBottomSheetLayoutButton.setOnClickListener {
            bottomDrawerDialog?.behavior?.isHideable = true
            dismissWithBehavior()
            viewModel.downloadCache()
        }
    }

    override fun onStart() {
        super.onStart()

        if(bottomDrawerDialog?.behavior?.state == STATE_COLLAPSED)
            bottomDrawerDialog?.behavior?.isHideable = false

        bottomDrawerDialog?.behavior?.addBottomSheetCallback(object : CustomBottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == CustomBottomSheetBehavior.STATE_COLLAPSED) {
                    bottomDrawerDialog?.behavior?.isHideable = false
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
    }

}