package com.example.stopwatchproject

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.stopwatchproject.presentation.stopwatchScreen.StopwatchScreen
import com.example.stopwatchproject.presentation.stopwatchScreen.StopwatchViewModel
import com.example.stopwatchproject.stopwatch.StopwatchNotificationManager.Companion.NOTIFICATION_TITLE_KEY
import com.example.stopwatchproject.stopwatch.StopwatchService
import com.example.stopwatchproject.ui.theme.StopwatchProjectTheme


class MainActivity : ComponentActivity() {
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkNotificationPermission()

        val viewModel = StopwatchViewModel()

        enableEdgeToEdge()
        setContent {
            StopwatchProjectTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    StopwatchScreen(
                        state = viewModel.state.collectAsState().value,
                        modifier = Modifier.padding(innerPadding),
                        startService = {
                            val intent = Intent(this, StopwatchService::class.java).apply {
                                putExtra(
                                    NOTIFICATION_TITLE_KEY,
                                    "Hello"
                                )
                            }
                            this.startForegroundService(intent)
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
