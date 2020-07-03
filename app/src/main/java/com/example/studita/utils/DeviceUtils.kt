package com.example.studita.utils

import android.annotation.SuppressLint
import android.app.Application
import android.provider.Settings

object DeviceUtils {
    @SuppressLint("HardwareIds")
    fun getDeviceId(applicationRef: Application): String{
        return Settings.Secure.getString(applicationRef.contentResolver, Settings.Secure.ANDROID_ID)
    }
}