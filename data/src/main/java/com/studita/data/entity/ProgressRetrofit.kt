package com.studita.data.entity

import com.studita.data.net.progress.ProgressInterceptor
import com.studita.data.net.progress.ProgressResponseBody
import retrofit2.Retrofit

data class ProgressRetrofit(val retrofit: Retrofit, var progressInterceptor: ProgressInterceptor)

fun ProgressRetrofit.setProgressRetrofitListener(progressListener: ProgressResponseBody.ProgressListener){
    this.progressInterceptor.progressListener = progressListener
}