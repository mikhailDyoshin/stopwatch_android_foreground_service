package com.example.stopwatchproject.stopwatch

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Process.THREAD_PRIORITY_BACKGROUND
import android.widget.Toast
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import com.example.stopwatchproject.StopwatchNotificationManager
import com.example.stopwatchproject.StopwatchStateFlowRepository
import com.example.stopwatchproject.common.createServiceLog

class StopwatchService : Service() {
    private val context = this

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null
    private var stopwatchNotificationManager: StopwatchNotificationManager? = null

    private val isRunning = mutableStateOf(false)

    private val _timeState = mutableLongStateOf(0L)

    // Handler that receives messages from the thread
    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            try {
                stopwatchNotificationManager?.setUpNotification()
                setIsRunningValue(true)
                incrementTime()
            } catch (e: InterruptedException) {
                // Restore interrupt status.
                Thread.currentThread().interrupt()
            }
        }
    }

    override fun onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread("ServiceStartArguments", THREAD_PRIORITY_BACKGROUND).apply {
            start()

            // Get the HandlerThread's Looper and use it for our Handler
            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
            stopwatchNotificationManager = StopwatchNotificationManager(
                context = context,
                notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            )
        }
        createServiceLog(context = this, message = "Service created")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {


        if (intent.action == StopwatchNotificationManager.StopwatchNotificationAction.ACTION_STOP.actionString) {
            stopSelf()
            createServiceLog(context = this, message = "Service canceled")
            return START_NOT_STICKY
        }

        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()
        createServiceLog(context = this, message = "Service started")

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        serviceHandler?.obtainMessage()?.also { msg ->
            msg.arg1 = startId
            serviceHandler?.sendMessage(msg)
        }

        // If we get killed, after returning from here, restart
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        setIsRunningValue(false)
        resetTime()
        emitTimeState(0L)
        stopwatchNotificationManager?.cancel()
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show()
        createServiceLog(context = this, message = "Service destroyed")
    }

    private fun incrementTime() {
        Thread.sleep(1000)
        while (isRunning.value) {
            incrementTimeValue()
            emitTimeState(_timeState.longValue)
            createServiceLog(
                context = this,
                message = "Service's running: ${_timeState.longValue}"
            )
            Thread.sleep(1000)
        }
    }

    private fun emitTimeState(timeValue: Long) {
        StopwatchStateFlowRepository.updateTime(timeValue)
    }

    private fun setIsRunningValue(value: Boolean) {
        isRunning.value = value
    }

    private fun resetTime() {
        _timeState.longValue = 0L
    }

    private fun incrementTimeValue() {
        _timeState.longValue += 1
    }
}
