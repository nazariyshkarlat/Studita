package com.example.studita.utils

import kotlinx.coroutines.*

fun CoroutineScope.launchExt(job: Job?,
    block: suspend CoroutineScope.() -> Unit
): Job {
    job?.cancel()
    return launch {
        block()
    }
}