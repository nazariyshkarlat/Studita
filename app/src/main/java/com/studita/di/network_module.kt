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
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.studita.data.entity.ProgressRetrofit
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.core.context.GlobalContext.get
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

private const val BASE_URL = "http://37.53.93.223:34867"

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

@ExperimentalSerializationApi
private fun provideProgressRetrofit(okHttpClient: OkHttpClient, progressInterceptor: ProgressInterceptor) =
    ProgressRetrofit(
        Retrofit.Builder()
            .client(
                okHttpClient
            )
            .addConverterFactory(provideConverterFactory())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(BASE_URL)
            .build(),
        progressInterceptor
    )

private fun provideRetrofit(okHttpClient: OkHttpClient) =
    Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(provideConverterFactory())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(BASE_URL)
        .build()

private fun provideConverterFactory() = Json{
    ignoreUnknownKeys=true
    encodeDefaults=true
}.asConverterFactory("application/json".toMediaType())

private fun provideHttpClient(loggingInterceptor: okhttp3.Interceptor) =
    OkHttpClient().newBuilder()
        .addInterceptor(loggingInterceptor)
        .readTimeout(1, TimeUnit.MINUTES)
        .connectTimeout(2, TimeUnit.SECONDS)
        .build()

private fun provideProgressHttpClient(loggingInterceptor: okhttp3.Interceptor, progressInterceptor: ProgressInterceptor) =
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
