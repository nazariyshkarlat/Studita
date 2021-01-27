package com.studita.presentation.fragments.first_open

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.studita.R
import com.studita.utils.replace
import kotlinx.android.synthetic.main.select_course_start_layout.*

class SelectCourseStartFragment : com.studita.presentation.fragments.base.BaseFragment(R.layout.select_course_start_layout){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectCourseStartLayoutButton.setOnClickListener {
            (activity as AppCompatActivity).replace(OfflineModeDownloadFragment(), R.id.frameLayout, addToBackStack = false)
        }
    }

}