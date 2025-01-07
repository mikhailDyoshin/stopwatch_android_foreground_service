package com.example.stopwatchproject.stopwatch.state

sealed class StopwatchResultState {
    data class Success(val totalTimeInMinutes: Long) : StopwatchResultState()
    data class Error(val message: String) : StopwatchResultState()
}