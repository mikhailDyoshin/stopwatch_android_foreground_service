package com.example.stopwatchproject.presentation.stopwatchScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stopwatchproject.StopwatchStateFlowRepository
import com.example.stopwatchproject.presentation.stopwatchScreen.state.ButtonState
import com.example.stopwatchproject.presentation.stopwatchScreen.state.StopwatchScreenState
import com.example.stopwatchproject.stopwatch.state.StopwatchState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class StopwatchViewModel : ViewModel() {

    val state =
        StopwatchStateFlowRepository.stopwatchState.map { mapToStopwatchScreenState(it) }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            StopwatchScreenState()
        )

    private fun mapToStopwatchScreenState(stopwatchState: StopwatchState): StopwatchScreenState {
        return when (stopwatchState) {
            is StopwatchState.Error -> StopwatchScreenState(
                titleText = stopwatchState.message,
                buttonState = ButtonState.IDLE
            )

            StopwatchState.Idle -> StopwatchScreenState(titleText = "0", buttonState = ButtonState.IDLE)
            is StopwatchState.Running -> StopwatchScreenState(
                titleText = stopwatchState.time.toString(),
                buttonState = ButtonState.ACTIVE
            )

            StopwatchState.Started -> StopwatchScreenState(titleText = "0", buttonState = ButtonState.ACTIVE)
            StopwatchState.Stopped -> StopwatchScreenState(titleText = "0", buttonState = ButtonState.IDLE)
        }
    }

}