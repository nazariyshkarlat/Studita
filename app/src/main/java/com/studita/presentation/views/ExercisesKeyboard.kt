package com.studita.presentation.views

import android.content.Context
import android.text.InputFilter
import android.text.SpannedString
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.view.children
import com.studita.R
import com.studita.presentation.views.press_view.PressTextView
import com.studita.utils.clear
import com.studita.utils.makeView
import com.studita.utils.removeLastChar
import com.studita.utils.replaceLastChar

class ExercisesKeyboard@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr){

    private var syncedView: TextView? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.exercises_keyboard, this)
    }

    fun syncWithTextView(textView: TextView){
        syncedView = textView
        setButtonsClick()
    }

    private fun setButtonsClick(){
        (this.getChildAt(0) as ViewGroup).children.forEach { child ->

            if (child is TextView) {

                var text: CharSequence? = child.text
                if (syncedView?.keyListener is DigitsKeyListener) {
                    val out: CharSequence? = (syncedView?.keyListener as DigitsKeyListener).filter(
                        child.text,
                        0,
                        child.text.length,
                        SpannedString(""),
                        0,
                        0
                    )
                    if (out != null)
                        text = out
                }

                if (!text.isNullOrEmpty()) {
                    if (child is PressTextView) {
                        child.setOnClickListener {
                            onButtonWithTextClick(child)
                        }
                    } else {
                        child.setOnClickListener {
                            onButtonWithTextClick(child)
                        }
                    }
                } else {
                    child.isEnabled = false
                }
            }else{
                child.setOnClickListener {
                    if(!syncedView?.text.isNullOrEmpty())
                        syncedView?.removeLastChar()
                }
                child.setOnLongClickListener {
                    if(!syncedView?.text.isNullOrEmpty()){
                    syncedView?.clear()
                        return@setOnLongClickListener true
                    }else
                        return@setOnLongClickListener false
                }
            }
        }
    }

    private fun onButtonWithTextClick(child: TextView){
        when {
            syncedView?.filters?.firstOrNull { it is InputFilter.LengthFilter && it.max == 1} != null -> {
                syncedView?.text = child.text
            }
            (syncedView?.text?.lastOrNull()?.isDigit() == false) && (!child.text.last().isDigit()) -> {
                syncedView?.replaceLastChar(child.text.last())
            }
            else -> syncedView?.append(child.text)
        }
    }
}