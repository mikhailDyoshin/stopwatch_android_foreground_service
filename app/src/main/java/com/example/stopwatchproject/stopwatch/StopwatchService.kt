package com.example.stopwatchproject.stopwatch

import android.annotation.SuppressLint
import android.app.ForegroundServiceStartNotAllowedException
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Process.THREAD_PRIORITY_BACKGROUND
import android.widget.Toast
import androidx.core.app.ServiceCompat
import com.example.stopwatchproject.stopwatch.StopwatchNotificationManager.Companion.NOTIFICATION_ID
import com.example.stopwatchproject.common.createServiceLog
import com.example.stopwatchproject.stopwatch.state.StopwatchState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@SuppressLint("ForegroundServiceType")
class StopwatchService : Service() {
    private val context = this

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null
    private var stopwatchNotificationManager: StopwatchNotificationManager? = null

    private fun updateNotification() {
        Stopwatch.state.onEach {
            if (it is StopwatchState.Running) stopwatchNotificationManager?.updateNotification(
                it.time
            )
        }.launchIn(scope)
    }


    private fun startForeground() {

        try {
            val notification = stopwatchNotificationManager?.getNotificationBuilder()?.build()

            if (notification != null) {
                ServiceCompat.startForeground(
                    this,
                    NOTIFICATION_ID,
                    notification,
                    0
                )
            }
        } catch (e: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && e is ForegroundServiceStartNotAllowedException
            ) {
                Stopwatch.stop(error = e.message)
                Toast.makeText(this, "Can't start service", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Handler that receives messages from the thread
    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            try {
                stopwatchNotificationManager?.setUpNotification()
                startForeground()
                Stopwatch.start()
            } catch (e: InterruptedException) {
                createServiceLog(
                    context = context,
                    message = "Exception occurred: $e"
                )
                Stopwatch.stop(error = e.message)
                Thread.currentThread().interrupt()
            }
        }
    }

    override fun onCreate() {
        Stopwatch.setUp()

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
        updateNotification()
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
        // Canceling all works
        Stopwatch.stop()
        stopwatchNotificationManager?.cancel()
        job.cancel()

        // Logging
        Toast.makeText(this, "Stopped", Toast.LENGTH_SHORT).show()
        createServiceLog(context = this, message = "Service destroyed")
    }

}
