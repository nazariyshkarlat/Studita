package com.studita.utils

import android.annotation.SuppressLint
import android.app.Application
import android.provider.Settings
import com.studita.domain.entity.UserData
import com.studita.domain.entity.UserIdTokenData
import com.studita.presentation.fragments.base.BaseFragment
import com.studita.presentation.fragments.main.HomeFragment

object DeviceUtils {
    @SuppressLint("HardwareIds")
    fun getDeviceId(applicationRef: Application): String {
        return Settings.Secure.getString(applicationRef.contentResolver, Settings.Secure.ANDROID_ID)
    }
}