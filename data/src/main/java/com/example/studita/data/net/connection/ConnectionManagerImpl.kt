package com.example.studita.data.net.connection

import android.net.ConnectivityManager
import com.example.studita.data.net.connection.ConnectionManager

class ConnectionManagerImpl(private val connectivityManager: ConnectivityManager?) :
    ConnectionManager {

    override fun isNetworkAbsent(): Boolean {
        val netInfo = connectivityManager?.activeNetworkInfo
        return netInfo == null || !netInfo.isConnected
    }
}