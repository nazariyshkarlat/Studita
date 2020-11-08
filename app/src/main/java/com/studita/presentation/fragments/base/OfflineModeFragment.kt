package com.studita.presentation.fragments.base

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.studita.R
import com.studita.utils.PrefsUtils
import com.studita.utils.navigateTo
import com.studita.utils.removeFragment
import com.studita.utils.replace
import kotlinx.android.synthetic.main.offline_mode_layout.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

open class OfflineModeFragment(private val default: KClass<out Fragment>) : NavigatableFragment(R.layout.offline_mode_layout){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        offlineModeLayoutButton.setOnClickListener {
            PrefsUtils.setOfflineMode(false)

            with((activity as AppCompatActivity)) {
                removeFragment(this@OfflineModeFragment)
                supportFragmentManager.popBackStack()
            }

            (activity as AppCompatActivity).replace(default.createInstance().apply {
                arguments = this@OfflineModeFragment.arguments
            }, (view.parent as FrameLayout).id, addToBackStack = true)
            }
    }

}