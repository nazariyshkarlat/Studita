package com.example.studita.presentation.views

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.InputFilter
import android.text.SpannedString
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.view.children
import com.example.studita.R
import com.example.studita.presentation.views.press_view.PressTextView
import com.example.studita.utils.clear
import com.example.studita.utils.makeView
import com.example.studita.utils.removeLastChar
import org.w3c.dom.Text

class ExercisesKeyboard@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr){

    private val keyboardView: GridLayout = makeView(R.layout.exercises_keyboard) as GridLayout
    private var syncedView: TextView? = null

    init {
        this.addView(keyboardView)
    }

    fun syncWithTextView(textView: TextView){
        syncedView = textView
        setButtonsClick()
    }

    private fun setButtonsClick(){
        keyboardView.children.forEach { child ->

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
                            onTextViewClick(child)
                        }
                    } else {
                        child.setOnClickListener {
                            onTextViewClick(child)
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

    private fun onTextViewClick(child: TextView){
        if(syncedView?.filters?.firstOrNull { it is InputFilter.LengthFilter && it.max == 1} != null) {
            if(child.text != "0")
                syncedView?.text = child.text
        }else
            syncedView?.append(child.text)
    }
}