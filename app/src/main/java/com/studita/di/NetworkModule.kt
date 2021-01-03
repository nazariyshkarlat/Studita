package com.studita.di

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.studita.data.BuildConfig
import com.studita.data.net.connection.ConnectionManager
import com.studita.data.net.connection.ConnectionManagerImpl
import com.studita.data.net.progress.ProgressInterceptor
import com.studita.data.net.progress.ProgressResponseBody
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {

    private const val BASE_URL = "http://37.53.93.223:34867"

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
                    getLoggingInterceptor()
                )
            )
    }

    fun <T> getService(className: Class<T>): T = retrofit.create(className)

    fun <T> getService(className: Class<T>, retrofit: Retrofit): T = retrofit.create(className)

    private fun getConnectivityManager(context: Context) =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun getProgressRetrofit(): ProgressRetrofit {
        val progressInterceptor = getProgressInterceptor()
        return ProgressRetrofit(
            Retrofit.Builder()
                .client(
                    getOkHttpProgressClient(
                        getLoggingInterceptor(), progressInterceptor
                    )
                )
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .baseUrl(BASE_URL)
                .build(),
            progressInterceptor as ProgressInterceptor
        )
    }

    fun ProgressRetrofit.setProgressRetrofitListener(progressListener: ProgressResponseBody.ProgressListener){
        this.progressInterceptor.progressListener = progressListener
    }

    private fun getRetrofit(client: OkHttpClient) =
        Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(BASE_URL)
            .build()

    private fun getOkHttpClient(loggingInterceptor: okhttp3.Interceptor) =
        OkHttpClient().newBuilder()
            .addInterceptor(loggingInterceptor)
            .readTimeout(1, TimeUnit.MINUTES)
            .connectTimeout(2, TimeUnit.SECONDS)
            .build()

    private fun getOkHttpProgressClient(loggingInterceptor: okhttp3.Interceptor, progressInterceptor: Interceptor) =
        OkHttpClient().newBuilder()
            .addInterceptor(loggingInterceptor)
            .addNetworkInterceptor(progressInterceptor)
            .readTimeout(1, TimeUnit.MINUTES)
            .connectTimeout(2, TimeUnit.SECONDS)
            .build()

    private fun getLoggingInterceptor(): okhttp3.Interceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BASIC
            else
                HttpLoggingInterceptor.Level.NONE
        }

    private fun getProgressInterceptor(): okhttp3.Interceptor =
        ProgressInterceptor()

    data class ProgressRetrofit(val retrofit: Retrofit, var progressInterceptor: ProgressInterceptor)
}