package com.example.studita.presentation.fragments.error_fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.studita.R
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.fragments.main.HomeFragment
import com.example.studita.presentation.listeners.OnSingleClickListener.Companion.setOnSingleClickListener
import com.example.studita.presentation.listeners.ReloadPageCallback
import com.example.studita.presentation.views.CustomSnackbar
import com.example.studita.utils.*
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
            chapterBottomSheetServerProblemsLayoutTryAgainButton.animateRefreshButton()
            lifecycleScope.launch(Dispatchers.Main) {
                delay(400)
                (parentFragment as? ReloadPageCallback)?.onPageReload()
                fragmentManager!!.removeFragment(this@ChapterBottomSheetServerProblemsFragment)
            }
        }

        chapterBottomSheetServerProblemsLayoutEnableOfflineModeButton.setOnClickListener {
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