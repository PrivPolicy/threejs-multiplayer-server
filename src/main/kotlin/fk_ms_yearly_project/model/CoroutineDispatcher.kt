package main.kotlin.fk_ms_yearly_project.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

object CoroutineDispatcher {
    fun dispatch(function: suspend () -> Unit): Job {
        return CoroutineScope(EmptyCoroutineContext).launch {
            function()
        }
    }
}