package com.example.stopwatchproject

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.stopwatchproject.stopwatch.StopwatchService
import com.example.stopwatchproject.ui.theme.StopwatchProjectTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkNotificationPermission()

        val viewModel = StopwatchViewModel(StopwatchStateFlowRepository)

        enableEdgeToEdge()
        setContent {
            StopwatchProjectTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Greeting(
                        state = viewModel.state.collectAsState().value,
                        modifier = Modifier.padding(innerPadding),
                        startService = {
                            startService(Intent(this, StopwatchService::class.java))
                        },
                        stopService = {
                            stopService(Intent(this, StopwatchService::class.java))
                        }
                    )
                }
            }
        }
    }

    private fun checkNotificationPermission() {
        // Check and request notification permissions for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_NOTIFICATIONS
                )
            }
        }
    }

    companion object {
        const val REQUEST_CODE_NOTIFICATIONS = 1001
    }
}

@Composable
fun Greeting(
    state: Long,
    modifier: Modifier = Modifier,
    startService: () -> Unit,
    stopService: () -> Unit
) {

    Column(
        modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = state.toString())
        Button(onClick = { startService() }) {
            Text(text = "Start Service")
        }
        Button(onClick = { stopService() }) {
            Text(text = "Stop Service")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StopwatchProjectTheme {
        Greeting(state = 0, startService = {}, stopService = {})
    }
}