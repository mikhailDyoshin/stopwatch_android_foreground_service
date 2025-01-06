package com.example.stopwatchproject.stopwatch

import androidx.compose.runtime.mutableLongStateOf
import com.example.stopwatchproject.common.StateUpdater
import com.example.stopwatchproject.stopwatch.state.StopwatchState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object Stopwatch {
    private val _state: MutableStateFlow<StopwatchState> =
        MutableStateFlow(StopwatchState.Idle)
    val state: StateFlow<StopwatchState> get() = _state

    private val timeInSecondsState = mutableLongStateOf(0L)

    private const val STOPWATCH_TIME_INTERVAL_IN_MS = 1000L

    private val stateUpdater = StateUpdater(
        callBack = {
            incrementTime()
            _state.tryEmit(
                StopwatchState.Running(
                    time = timeInSecondsState.longValue
                )
            )
        },
        updatePeriodMillis = STOPWATCH_TIME_INTERVAL_IN_MS
    )

    fun setUp() {
        _state.tryEmit(StopwatchState.Idle)
    }

    fun start() {
        _state.tryEmit(StopwatchState.Started)
        stateUpdater.start()
    }

    fun stop() {
        stateUpdater.stop()
        resetTime()
        _state.tryEmit(StopwatchState.Stopped)
    }

    private fun incrementTime() {
        timeInSecondsState.value += 1
    }

    private fun resetTime() {
        timeInSecondsState.longValue = 0L
    }
}
