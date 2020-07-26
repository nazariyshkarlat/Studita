package com.example.studita.utils

import com.example.studita.utils.TextUtils.isFullNameCharAllowed

object TextUtils {

    fun encryptEmail(email: String): String{
        val parts = email.split("@")
        val firstPart = parts[0].substring(parts[0].length-3, parts[0].length)
        val secondPart = parts[1]
        return "***$firstPart@$secondPart"
    }

    fun Char.isSearchCharAllowed(previousChar: Char?) = (this.isWhitespace() && previousChar?.isWhitespace() == false) || this.isLetterOrDigit()

    fun Char.isFullNameCharAllowed(previousChar: Char?) = (this.isWhitespace() && previousChar?.isWhitespace() == false) || this.isLetter()
}