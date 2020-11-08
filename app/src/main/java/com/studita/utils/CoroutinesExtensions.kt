package com.studita.utils

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

fun CoroutineScope.launchExt(
    job: Job?,
    coroutineContext: CoroutineContext = Dispatchers.Main,
    block: suspend CoroutineScope.() -> Unit
): Job {
    job?.cancel()
    return launch(coroutineContext) {
        block()
    }
}

fun CoroutineScope.launchBlock(
    job: Job?,
    coroutineContext: CoroutineContext = Dispatchers.Main,
    block: suspend CoroutineScope.() -> Unit
): Job {
    return if (job == null || job.isCompleted) {
        launch(coroutineContext) {
            block()
        }
    } else
        job
}

fun CoroutineScope.asyncExt(
    job: Job?,
    block: suspend CoroutineScope.() -> Unit
): Job {
    job?.cancel()
    return async {
        block()
    }
}