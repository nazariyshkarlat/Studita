package com.example.studita.presentation.activities

import android.net.Uri
import android.os.Bundle
import com.example.studita.R
import com.example.studita.utils.navigateTo

class ShareActivity : DefaultActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frame_layout)

        val data: Uri? = intent?.data

        println(data?.queryParameterNames)
    }

}