package com.example.stopwatchproject

import android.util.Log
import com.example.stopwatchproject.stopwatch.state.StopwatchState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object StopwatchStateFlowRepository {

    private const val TAG = "StopwatchFlow"

    private val _stopwatchState: MutableStateFlow<StopwatchState> =
        MutableStateFlow(StopwatchState.Idle)
    val stopwatchState: StateFlow<StopwatchState> get() = _stopwatchState

    init {
        Log.d(TAG, "Stopwatch state-flow created")
    }

    fun updateState(newState: StopwatchState) {
        _stopwatchState.value = newState
        Log.d(TAG, newState.toString())
    }

}