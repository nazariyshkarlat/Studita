package com.studita.di

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import com.studita.data.BuildConfig
import com.studita.data.net.connection.ConnectionManager
import com.studita.data.net.connection.ConnectionManagerImpl
import com.studita.data.net.progress.ProgressInterceptor
import com.studita.data.net.progress.ProgressResponseBody
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.studita.data.entity.ProgressRetrofit
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.core.context.GlobalContext.get
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier

private const val BASE_URL = "http://10.0.2.2:5000"

val networkModule = module {

    single(named("progress_retrofit")) {
        provideProgressRetrofit(get(named("progress_client")), get(named("progress_interceptor")))
    }

    single {
        provideRetrofit(get())
    }

    single {
        ConnectionManagerImpl(
            get()
        )
    } bind(ConnectionManager::class)

    single {
        androidApplication().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    single {
        provideHttpClient(get(named("logging_interceptor")))
    }

    single(named("progress_client")) {
        provideProgressHttpClient(get(named("logging_interceptor")), get(named("progress_interceptor")))
    }

    factory(named("logging_interceptor")) { provideHttpLoggingInterceptor() } bind (Interceptor::class)

    single (named("progress_interceptor")) { ProgressInterceptor() }
}

fun <T> getService(className: Class<T>): T = get().get<Retrofit>().create(className)

fun <T> getService(className: Class<T>, retrofit: Retrofit): T = retrofit.create(className)

private fun provideProgressRetrofit(okHttpClient: OkHttpClient, progressInterceptor: ProgressInterceptor) =
    ProgressRetrofit(
        Retrofit.Builder()
            .client(
                okHttpClient
            )
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(BASE_URL)
            .build(),
        progressInterceptor
    )

private fun provideRetrofit(okHttpClient: OkHttpClient) =
    Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(BASE_URL)
        .build()

private fun provideHttpClient(loggingInterceptor: okhttp3.Interceptor) =
    OkHttpClient().newBuilder()
        .addInterceptor(loggingInterceptor)
        .readTimeout(1, TimeUnit.MINUTES)
        .connectTimeout(2, TimeUnit.SECONDS)
        .build()

private fun provideProgressHttpClient(loggingInterceptor: okhttp3.Interceptor, progressInterceptor: Interceptor) =
    OkHttpClient().newBuilder()
        .addInterceptor(loggingInterceptor)
        .addNetworkInterceptor(progressInterceptor)
        .readTimeout(1, TimeUnit.MINUTES)
        .connectTimeout(2, TimeUnit.SECONDS)
        .build()

private fun provideHttpLoggingInterceptor() =
    HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG)
            HttpLoggingInterceptor.Level.BASIC
        else
            HttpLoggingInterceptor.Level.NONE
    }
