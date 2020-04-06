package com.example.studita.presentation.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

fun CoroutineScope.launchExt(job: Job?,
    block: suspend CoroutineScope.() -> Unit
): Job {
    job?.cancel()
    return launch {
        block()
    }
}