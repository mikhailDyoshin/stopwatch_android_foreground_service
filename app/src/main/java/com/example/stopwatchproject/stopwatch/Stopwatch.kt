package com.example.stopwatchproject.stopwatch

import androidx.compose.runtime.mutableLongStateOf
import com.example.stopwatchproject.stopwatch.utils.StateUpdater
import com.example.stopwatchproject.stopwatch.state.StopwatchResultState
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
            _state.value = StopwatchState.Running(time = timeInSecondsState.longValue)
        },
        updatePeriodMillis = STOPWATCH_TIME_INTERVAL_IN_MS
    )

    fun setUp() {
        _state.value = StopwatchState.Idle
    }

    fun start() {
        _state.value = StopwatchState.Started
        stateUpdater.start()
    }

    fun stop(error: String? = null) {
        stateUpdater.stop()
        _state.value =
            StopwatchState.Stopped(
                result = if (error != null) {
                    StopwatchResultState.Error(
                        message = error
                    )
                } else {
                    StopwatchResultState.Success(
                        totalTimeInMinutes = timeInSecondsState.longValue
                    )
                }
            )
        resetTime()
    }

    private fun incrementTime() {
        timeInSecondsState.value += 1
    }

    private fun resetTime() {
        timeInSecondsState.longValue = 0L
    }
}
