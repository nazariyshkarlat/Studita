package com.example.studita.presentation.fragments.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import com.example.studita.R
import kotlinx.android.synthetic.main.dialog_alert_layout.*


open class BaseDialogFragment(@LayoutRes private val layoutResId: Int) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_alert_background)
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            it.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    protected fun setText(title: String, subtitle: String, leftButton: String, rightButton: String){
        dialogAlertTitle.text = title
        dialogAlertSubtitle.text = subtitle
        dialogAlertLeftButton.text = leftButton
        dialogAlertRightButton.text = rightButton
    }

}