package com.example.stopwatchproject.presentation.stopwatchScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stopwatchproject.presentation.stopwatchScreen.state.ButtonState
import com.example.stopwatchproject.presentation.stopwatchScreen.state.StopwatchScreenState
import com.example.stopwatchproject.ui.theme.StopwatchProjectTheme

@Composable
fun StopwatchScreen(
    state: StopwatchScreenState,
    modifier: Modifier = Modifier,
    startService: () -> Unit,
    stopService: () -> Unit
) {

    Column(
        modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(text = state.titleState, fontSize = 48.sp)
        Button(onClick = {
                when (state.buttonState) {
                    ButtonState.ACTIVE -> stopService()
                    ButtonState.IDLE -> startService()
                }
            }
        ) {
            Text(
                text = state.buttonState.buttonText,
                fontSize = 32.sp,
                modifier = Modifier.padding(horizontal = 40.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StopwatchScreenPreview() {
    StopwatchProjectTheme {
        StopwatchScreen(state = StopwatchScreenState(), startService = {}, stopService = {})
    }
}