package com.example.studita.presentation.activities

import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.example.studita.R

class ShareActivity : DefaultActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frame_layout)

        val data: Uri? = intent?.data

        Log.d("SHARE_ACTIVITY", "QUERY PARAMS: ${data?.queryParameterNames}")
    }

}