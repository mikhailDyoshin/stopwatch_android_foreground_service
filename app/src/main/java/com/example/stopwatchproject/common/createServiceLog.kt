package com.example.stopwatchproject.common

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat.getString
import com.example.stopwatchproject.R

fun createServiceLog(context: Context, message: String) {
    Log.d(getString(context, R.string.stopwatch_service_tag), message)
}