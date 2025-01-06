package com.example.stopwatchproject.presentation.stopwatchScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stopwatchproject.StopwatchStateFlowRepository
import com.example.stopwatchproject.common.MilitaryTime
import com.example.stopwatchproject.presentation.stopwatchScreen.state.ButtonState
import com.example.stopwatchproject.presentation.stopwatchScreen.state.StopwatchScreenState
import com.example.stopwatchproject.stopwatch.Stopwatch
import com.example.stopwatchproject.stopwatch.state.StopwatchState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class StopwatchViewModel : ViewModel() {

    val state =
        Stopwatch.state.map { mapToStopwatchScreenState(it) }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            StopwatchScreenState()
        )

    private fun mapToStopwatchScreenState(stopwatchState: StopwatchState): StopwatchScreenState {
        return when (stopwatchState) {
            is StopwatchState.Error -> StopwatchScreenState(
                titleState = stopwatchState.message,
                buttonState = ButtonState.IDLE
            )

            StopwatchState.Idle -> StopwatchScreenState(
                titleState = MilitaryTime.secondsToMilitaryTime(
                    0L
                ).toString(), buttonState = ButtonState.IDLE
            )

            is StopwatchState.Running -> StopwatchScreenState(
                titleState = MilitaryTime.secondsToMilitaryTime(stopwatchState.time).toString(),
                buttonState = ButtonState.ACTIVE
            )

            StopwatchState.Started -> StopwatchScreenState(
                titleState = MilitaryTime.secondsToMilitaryTime(
                    0L
                ).toString(), buttonState = ButtonState.ACTIVE
            )

            StopwatchState.Stopped -> StopwatchScreenState(
                titleState = MilitaryTime.secondsToMilitaryTime(
                    0L
                ).toString(), buttonState = ButtonState.IDLE
            )
        }
    }

}