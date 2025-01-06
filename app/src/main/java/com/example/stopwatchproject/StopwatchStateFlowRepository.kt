package com.example.stopwatchproject

import com.example.stopwatchproject.stopwatch.state.StopwatchState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object StopwatchStateFlowRepository {
    private val _stopwatchState: MutableStateFlow<StopwatchState> =
        MutableStateFlow(StopwatchState.Idle)
    val stopwatchState: StateFlow<StopwatchState> get() = _stopwatchState

    fun updateState(newState: StopwatchState) {
        _stopwatchState.value = newState
    }
}