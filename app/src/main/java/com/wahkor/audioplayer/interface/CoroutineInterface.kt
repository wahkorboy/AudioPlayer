package com.wahkor.audioplayer.`interface`

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

interface CoroutineInterface {

    val modelJob: CompletableJob
    val mainScope: CoroutineScope
        get() = CoroutineScope(Dispatchers.Main + modelJob)
}