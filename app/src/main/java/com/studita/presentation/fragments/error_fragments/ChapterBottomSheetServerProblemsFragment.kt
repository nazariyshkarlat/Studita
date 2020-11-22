package com.studita.presentation.fragments.error_fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.studita.R
import com.studita.presentation.fragments.base.BaseFragment
import com.studita.presentation.fragments.main.HomeFragment
import com.studita.presentation.listeners.OnSingleClickListener.Companion.setOnSingleClickListener
import com.studita.presentation.listeners.ReloadPageCallback
import com.studita.presentation.views.CustomSnackbar
import com.studita.utils.*
import kotlinx.android.synthetic.main.chapter_bottom_sheet_internet_is_disabled_layout.*
import kotlinx.android.synthetic.main.chapter_bottom_sheet_server_problems_layout.*
import kotlinx.android.synthetic.main.chapter_layout.*
import kotlinx.android.synthetic.main.server_problems_main_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChapterBottomSheetServerProblemsFragment : BaseFragment(R.layout.chapter_bottom_sheet_server_problems_layout){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chapterBottomSheetServerProblemsLayoutTryAgainButton.setOnSingleClickListener {
            chapterBottomSheetServerProblemsLayoutEnableOfflineModeButton.setOnClickListener {  }
            chapterBottomSheetServerProblemsLayoutTryAgainButton.animateRefreshButton()
            lifecycleScope.launch(Dispatchers.Main) {
                delay(400)
                (parentFragment as? ReloadPageCallback)?.onPageReload()
                fragmentManager!!.removeFragment(this@ChapterBottomSheetServerProblemsFragment)
            }
        }

        chapterBottomSheetServerProblemsLayoutEnableOfflineModeButton.setOnClickListener {
            chapterBottomSheetServerProblemsLayoutTryAgainButton.setOnClickListener {  }
            PrefsUtils.setOfflineMode(true)
            removeFragmentWithAnimation(view)

            (parentFragment as? ReloadPageCallback)?.onPageReload()

            CustomSnackbar(context!!).show(
                resources.getString(R.string.enable_offline_mode_snackbar),
                ThemeUtils.getAccentColor(context!!),
                contentView = parentFragment?.chapterLayoutFrameLayout?.parent?.parent?.parent?.parent as ViewGroup
            )
        }

        view.setOnTouchListener { _, _ ->  true}
    }
}