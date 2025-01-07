package com.example.stopwatchproject.stopwatch.state

sealed class StopwatchState {
    data object Idle : StopwatchState()
    data object Started : StopwatchState()
    data class Running(val time: Long) : StopwatchState()
    data class Stopped(val result: StopwatchResultState) : StopwatchState()
}