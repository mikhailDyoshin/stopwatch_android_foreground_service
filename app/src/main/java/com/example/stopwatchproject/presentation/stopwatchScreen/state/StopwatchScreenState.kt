package com.example.stopwatchproject.presentation.stopwatchScreen.state

data class StopwatchScreenState(
    val titleText: String = "0",
    val buttonState: ButtonState = ButtonState.IDLE
)
