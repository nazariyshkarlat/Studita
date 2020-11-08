package com.studita.domain.validator


class AuthorizationValidator {

    companion object {
        const val MINIMUM_PASSWORD_LENGTH = 6
    }

    private fun String.isValidEmail(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    fun isValid(source: Pair<String, String>): Pair<Boolean, Boolean> {
        var result: Pair<Boolean, Boolean> = false to false
        if (source.first.isValidEmail()) {
            result = result.copy(first = true)
        }
        if (source.second.length >= MINIMUM_PASSWORD_LENGTH) {
            result = result.copy(second = true)
        }
        return result
    }
}