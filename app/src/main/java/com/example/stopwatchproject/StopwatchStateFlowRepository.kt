package com.example.stopwatchproject

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object StopwatchStateFlowRepository {
    private val _stopwatchState = MutableStateFlow(0L)
    val stopwatchState: StateFlow<Long> get() = _stopwatchState

    fun updateTime(seconds: Long) {
        _stopwatchState.value = seconds
    }
}