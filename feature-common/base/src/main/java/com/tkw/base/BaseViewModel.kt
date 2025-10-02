package com.tkw.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

open class BaseViewModel: ViewModel() {
    private val _alertFlow = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 1)
    val alertFlow: SharedFlow<String> = _alertFlow.asSharedFlow()

    private val _progressFlow = MutableSharedFlow<Boolean>(replay = 0, extraBufferCapacity = 1)
    val progressFlow: SharedFlow<Boolean> = _progressFlow.asSharedFlow()

    protected fun showAlert(msg: String) {
        _alertFlow.tryEmit(msg)
    }
}

fun ViewModel.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Unit
): Job = viewModelScope.launch(context + CoroutineExceptionHandler { _, throwable ->
    throwable.printStackTrace()
}) {
    block()
}