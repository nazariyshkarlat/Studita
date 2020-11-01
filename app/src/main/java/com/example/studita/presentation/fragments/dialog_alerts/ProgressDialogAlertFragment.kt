package com.example.studita.presentation.fragments.dialog_alerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.studita.R
import com.example.studita.presentation.fragments.base.BaseDialogFragment
import com.example.studita.presentation.fragments.base.BaseFragment

class ProgressDialogAlertFragment : DialogFragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = false
        return inflater.inflate(R.layout.progress_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    interface ProgressDialogAlertFragmentCancelCallback{

        fun onCancel()

    }

}