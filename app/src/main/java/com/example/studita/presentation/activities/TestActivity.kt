package com.example.studita.presentation.activities

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.example.studita.R
import kotlinx.android.synthetic.main.test_layout.*

class TestActivity : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.LightTheme)
        setContentView(R.layout.test_layout)
        exercisesKeyboardTest.syncWithTextView(textViewTest)
    }

}