package com.example.studita.presentation.listeners

import android.text.Editable

import android.text.TextWatcher
import android.view.View
import android.widget.EditText


interface GenericTextWatcher {
    fun beforeTextChanged(
        view: View,
        charSequence: CharSequence?,
        start: Int,
        count: Int,
        after: Int
    )

    fun onTextChanged(view: View, charSequence: CharSequence?, start: Int, before: Int, count: Int)
    fun afterTextChanged(view: View, editable: Editable?)
}

class GenericTextWatcherImpl(
    private val genericTextWatcher: GenericTextWatcher,
    private val view: View
) : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
        genericTextWatcher.afterTextChanged(view, s)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        genericTextWatcher.beforeTextChanged(view, s, start, count, after)
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        genericTextWatcher.onTextChanged(view, s, start, before, count)
    }


}

fun EditText.setGenericTextWatcher(genericTextWatcherImpl: GenericTextWatcherImpl) {
    addTextChangedListener(genericTextWatcherImpl)
}