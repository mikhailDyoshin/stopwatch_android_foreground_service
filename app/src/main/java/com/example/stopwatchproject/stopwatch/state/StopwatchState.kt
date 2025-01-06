package com.example.stopwatchproject.stopwatch.state

sealed class StopwatchState {
    data object Idle : StopwatchState()
    data object Started : StopwatchState()
    data class Running(val time: Long) : StopwatchState()
    data object Stopped : StopwatchState()
    data class Error(val message: String) : StopwatchState()
}