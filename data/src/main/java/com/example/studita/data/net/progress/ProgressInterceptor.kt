package com.example.studita.data.net.progress

import okhttp3.Interceptor
import okhttp3.Response

class ProgressInterceptor : Interceptor{

    lateinit var progressListener: ProgressResponseBody.ProgressListener

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        return originalResponse.newBuilder()
            .body(ProgressResponseBody(originalResponse.body(), progressListener)).build()
    }

}