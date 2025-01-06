package com.example.stopwatchproject.stopwatch

import android.app.ForegroundServiceStartNotAllowedException
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Process.THREAD_PRIORITY_BACKGROUND
import android.widget.Toast
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ServiceCompat
import com.example.stopwatchproject.StopwatchNotificationManager
import com.example.stopwatchproject.StopwatchNotificationManager.Companion.NOTIFICATION_ID
import com.example.stopwatchproject.StopwatchStateFlowRepository
import com.example.stopwatchproject.common.createServiceLog
import com.example.stopwatchproject.stopwatch.state.StopwatchState

class StopwatchService : Service() {
    private val context = this

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null
    private var stopwatchNotificationManager: StopwatchNotificationManager? = null

    private val isRunning = mutableStateOf(false)

    private val _timeState = mutableLongStateOf(0L)

    private fun startForeground() {

        try {
            val notification = stopwatchNotificationManager?.getNotificationBuilder()?.build()

            if (notification != null) {
                ServiceCompat.startForeground(
                    this,
                    NOTIFICATION_ID,
                    notification,
                    FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
                )
            }
        } catch (e: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && e is ForegroundServiceStartNotAllowedException
            ) {
                emitStopwatchState(StopwatchState.Error(message = "Can't start service"))
                Toast.makeText(this, "Can't start service", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Handler that receives messages from the thread
    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            try {
                stopwatchNotificationManager?.setUpNotification()
                setIsRunningValue(true)
                startForeground()
                emitStopwatchState(StopwatchState.Started)
                incrementTime()
            } catch (e: InterruptedException) {
                // Restore interrupt status.
                emitStopwatchState(StopwatchState.Error(message = "InterruptedException occurred: $e"))
                createServiceLog(
                    context = context,
                    message = "Exception occurred: $e"
                )
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

        Toast.makeText(this, "Started", Toast.LENGTH_SHORT).show()
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
        emitStopwatchState(StopwatchState.Stopped)
        emitStopwatchState(StopwatchState.Idle)
        stopwatchNotificationManager?.cancel()
        Toast.makeText(this, "Stopped", Toast.LENGTH_SHORT).show()
        createServiceLog(context = this, message = "Service destroyed")
    }

    private fun incrementTime() {
        Thread.sleep(1000)
        while (isRunning.value) {
            incrementTimeValue()
            emitStopwatchState(StopwatchState.Running(time = _timeState.longValue))
            stopwatchNotificationManager?.updateNotification(_timeState.longValue)
            createServiceLog(
                context = this,
                message = "Service's running: ${_timeState.longValue}"
            )
            Thread.sleep(1000)
        }
    }

    private fun emitStopwatchState(state: StopwatchState) {
        StopwatchStateFlowRepository.updateState(state)
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
