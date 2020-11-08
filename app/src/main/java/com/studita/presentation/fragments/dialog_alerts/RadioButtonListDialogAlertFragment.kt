package com.studita.presentation.fragments.dialog_alerts

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import com.studita.R
import com.studita.presentation.fragments.base.BaseDialogFragment
import com.studita.utils.makeView
import com.studita.utils.postExt
import kotlinx.android.synthetic.main.dialog_list_layout.*
import kotlinx.android.synthetic.main.dialog_list_layout_radio_button_item.view.*

open class RadioButtonListDialogAlertFragment : BaseDialogFragment(R.layout.dialog_list_layout) {

    var selectedPos = -1

    lateinit var items: List<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(savedInstanceState?.containsKey("SELECTED_POS") == true)
            selectedPos = savedInstanceState.getInt("SELECTED_POS")

        for (itemText in items) {
            val itemView =
                dialogListLayoutListLayout.makeView(R.layout.dialog_list_layout_radio_button_item)
            itemView.dialogListLayoutRadioButtonItemTextView.text = itemText
            dialogListLayoutListLayout.addView(itemView)
        }

        dialogListLayoutListLayout.postExt<ViewGroup> {
            it.children.forEachIndexed { index, child ->

                child.dialogListLayoutRadioButtonItemRadioButton.isChecked = index == selectedPos

                child.dialogListLayoutRadioButtonItemRadioButton.jumpDrawablesToCurrentState()
                child.setOnClickListener {
                    if (selectedPos != index) {
                        selectedPos = index
                        dialogListLayoutListLayout.children.forEach { child ->
                            child.dialogListLayoutRadioButtonItemRadioButton.isChecked = false
                        }
                        child.dialogListLayoutRadioButtonItemRadioButton.isChecked = true
                    }
                }
            }
        }

        dialogListLayoutLeftButton.setOnClickListener { dismiss() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState.apply {
            putInt("SELECTED_POS", selectedPos)
        })
    }
}