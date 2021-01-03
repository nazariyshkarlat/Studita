package com.studita.data.net.connection

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build


class ConnectionManagerImpl(private val connectivityManager: ConnectivityManager) :
    ConnectionManager {

    override fun isNetworkAbsent(): Boolean {
        val n = connectivityManager.activeNetwork
        if (n != null) {
            val nc = connectivityManager.getNetworkCapabilities(n)
            return !((nc?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true) ||
                    (nc?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true))
        }
        return true
    }
}