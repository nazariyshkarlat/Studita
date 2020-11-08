package com.studita.utils

object TextUtils {

    fun encryptEmail(email: String): String {
        val parts = email.split("@")
        val firstPart = parts[0].substring(parts[0].length - 3, parts[0].length)
        val secondPart = parts[1]
        return "***$firstPart@$secondPart"
    }
}