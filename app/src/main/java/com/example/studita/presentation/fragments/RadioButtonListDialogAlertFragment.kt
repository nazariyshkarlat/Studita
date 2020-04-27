package com.example.studita.presentation.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import com.example.studita.R
import com.example.studita.presentation.fragments.base.BaseDialogFragment
import com.example.studita.presentation.utils.makeView
import kotlinx.android.synthetic.main.dialog_list_layout.*
import kotlinx.android.synthetic.main.dialog_list_layout_radio_button_item.view.*

open class RadioButtonListDialogAlertFragment : BaseDialogFragment(R.layout.dialog_list_layout) {

    var selectedPos = -1

    lateinit var items: List<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        for (itemText in items) {
            val itemView =
                dialogListLayoutListLayout.makeView(R.layout.dialog_list_layout_radio_button_item)
            itemView.dialogListLayoutRadioButtonItemTextView.text = itemText
            dialogListLayoutListLayout.addView(itemView)
        }

        (dialogListLayoutListLayout as ViewGroup).children.forEachIndexed { index, child ->
            child.dialogListLayoutRadioButtonItemRadioButton.isChecked = index == selectedPos
            child.setOnClickListener {
                if(selectedPos != index) {
                    selectedPos = index
                    dialogListLayoutListLayout.children.forEach { child ->
                        child.dialogListLayoutRadioButtonItemRadioButton.isChecked = false
                    }
                    child.dialogListLayoutRadioButtonItemRadioButton.isChecked = true
                }
            }
        }

        dialogListLayoutLeftButton.setOnClickListener { dialog?.hide() }
    }
}