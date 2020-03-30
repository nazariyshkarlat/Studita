package com.example.studita.data.net

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

object NetworkUtils {
    fun isConnected(context: Context): Boolean {
        val info: NetworkInfo? = getNetworkInfo(context)
        return info != null && info.isConnected
    }

    private fun getNetworkInfo(context: Context): NetworkInfo? {
        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo
    }
}