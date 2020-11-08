package com.studita.data.net.connection

import android.net.ConnectivityManager

class ConnectionManagerImpl(private val connectivityManager: ConnectivityManager?) :
    ConnectionManager {

    override fun isNetworkAbsent(): Boolean {
        val netInfo = connectivityManager?.activeNetworkInfo
        return netInfo == null || !netInfo.isConnected
    }
}