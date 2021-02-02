package com.studita.presentation.views

import android.content.Context
import android.text.Editable
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import java.lang.Exception

class MathLineBreakingTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)

        post {
            if(!text.isNullOrEmpty()) {
                val newText: Editable = SpannableStringBuilder(text)
                for (lineNumber: Int in 1 .. lineCount) {
                    var endLineIndex: Int? = null
                    try {
                        endLineIndex = (layout.getLineEnd(lineNumber-1) - 1)
                    }catch(e: Exception) {
                        e.printStackTrace()
                    }
                    if(endLineIndex != null) {
                        var endChar = text[endLineIndex]
                        if (endLineIndex <= text.lastIndex) {
                            while (!endChar.fromUiToDomainOperator().isOperator() && endChar != '\n' && lineNumber != lineCount) {
                                endLineIndex--
                                endChar = text[endLineIndex]
                            }
                            if (endChar.isOperator()) {
                                newText.insert(endLineIndex+1, "\n")
                                newText.insert(endLineIndex+2, endChar.toString())
                                setText(newText)
                            }
                        }
                    }
                }
            }
        }
    }

    fun Char.toOperator() = when (this) {
        '+' -> Operator.PLUS
        '-' -> Operator.MINUS
        '*' -> Operator.MULTIPLY
        '/' -> Operator.DIVIDE
        else -> throw UnsupportedOperationException("unknown operator")
    }

    fun Char.isOperator() = this == Operator.PLUS.toCharacter() ||
            this == Operator.MINUS.toCharacter() ||
            this == Operator.DIVIDE.toCharacter() ||
            this == Operator.MULTIPLY.toCharacter()

    fun Operator.toCharacter() = when (this) {
        Operator.PLUS -> '+'
        Operator.MINUS -> '-'
        Operator.MULTIPLY -> '*'
        Operator.DIVIDE -> '/'
    }

    enum class Operator {
        PLUS,
        MINUS,
        MULTIPLY,
        DIVIDE
    }
    
    private fun Char.fromUiToDomainOperator() = when {
        this == '÷' -> Operator.DIVIDE.toCharacter()
        this == '×' -> Operator.MULTIPLY.toCharacter()
        else -> this
    }
}