package com.example.stopwatchproject.presentation.stopwatchScreen.state

import com.example.stopwatchproject.stopwatch.utils.MilitaryTime

data class StopwatchScreenState(
    val titleState: String = MilitaryTime.secondsToMilitaryTime(0L).toString(),
    val buttonState: ButtonState = ButtonState.IDLE
)
