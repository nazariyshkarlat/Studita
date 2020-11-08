package com.studita.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.studita.di.NetworkModule


object NetworkUtils {

    private val networkLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun getNetworkLiveData(context: Context): LiveData<Boolean> {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (NetworkModule.connectionManager.isNetworkAbsent())
            networkLiveData.value = false

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network?) {
                networkLiveData.postValue( true)
            }

            override fun onLost(network: Network?) {
                networkLiveData.postValue( false)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            val builder = NetworkRequest.Builder()
            connectivityManager.registerNetworkCallback(builder.build(), networkCallback)
        }

        return networkLiveData
    }
}