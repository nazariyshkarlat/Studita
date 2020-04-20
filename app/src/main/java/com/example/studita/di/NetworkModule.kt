package com.example.studita.di

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.example.data.BuildConfig
import com.example.studita.data.net.connection.ConnectionManagerImpl
import com.example.studita.data.net.connection.ConnectionManager
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {

    private const val BASE_URL = "http://10.0.2.2:5000"

    lateinit var connectionManager: ConnectionManager
    private lateinit var retrofit: Retrofit

    lateinit var context: Context

    fun initialize(app: Application) {
        context = app
        connectionManager =
            ConnectionManagerImpl(
                getConnectivityManager(app)
            )
        retrofit =
            getRetrofit(
                getOkHttpClient(
                    getInterceptor()
                )
            )
    }

    fun <T> getService(className: Class<T>): T = retrofit.create(className)

    private fun getConnectivityManager(context: Context) =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

    private fun getRetrofit(client: OkHttpClient) =
        Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

    private fun getOkHttpClient(interceptor: okhttp3.Interceptor) =
        OkHttpClient().newBuilder()
            .addInterceptor(interceptor)
            .readTimeout(1, TimeUnit.MINUTES)
            .connectTimeout(1, TimeUnit.MINUTES)
            .build()


    private fun getInterceptor(): okhttp3.Interceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        }

}