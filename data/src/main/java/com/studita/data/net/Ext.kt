package com.studita.data.net

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@ExperimentalCoroutinesApi
fun <T> Call<T>.asFlow(): Flow<T> = callbackFlow {
    enqueue(object : Callback<T>{

        override fun onResponse(call: Call<T>, response: Response<T>) {
            if(response.isSuccessful){
                offer(response.body())
                close()
            }else{
                close(RetrofitError(response.errorBody()))
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            close(t)
        }
    })

    awaitClose {
        if(!isCanceled)
            cancel()
    }
}

class RetrofitError(val errorBody: ResponseBody?): Throwable()