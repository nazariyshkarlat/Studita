package com.example.studita.utils

import com.google.android.gms.tasks.Tasks.await
import kotlinx.coroutines.*

fun CoroutineScope.launchExt(job: Job?,
    block: suspend CoroutineScope.() -> Unit
): Job {
    job?.cancel()
    return launch {
        block()
    }
}

fun CoroutineScope.asyncExt(job: Job?,
                             block: suspend CoroutineScope.() -> Unit
): Job {
    job?.cancel()
    return async {
        block()
    }
}