package com.example.stopwatchproject.stopwatch

import android.app.Service
import android.content.Intent
import android.os.IBinder

class StopwatchService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}