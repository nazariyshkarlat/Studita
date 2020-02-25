package com.example.studita.presentation.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity

inline fun <reified T : AppCompatActivity> AppCompatActivity.startActivity(){
    val intent = Intent(this, T::class.java)
    this.startActivity(intent)
}

fun Activity.hideKeyboard() {
    try {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        if (currentFocus != null && currentFocus?.windowToken != null) {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                currentFocus?.windowToken, 0
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

}