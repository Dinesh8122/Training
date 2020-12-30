package com.example.training

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.SystemClock
import android.util.Log


class TimerService : Service() {
    private var intent: Intent? = null
    private val handler: Handler = Handler()
    var initial_time = 0L
    var timeInMilliseconds = 0L
    var timeSwapBuff = 0L
    var updatedTime = 0L


    override fun onCreate() {
        super.onCreate()
        val sharePerfereneces = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        initial_time = SystemClock.uptimeMillis()
        timeSwapBuff += sharePerfereneces.getLong("time", 0);
        Log.i("TimerService", "onCreate: initial_time $initial_time")
        if (initial_time == 0.toLong()) {
            initial_time = SystemClock.uptimeMillis()
            Log.i("TimerService", "onCreate: if condition initial_time $initial_time")

        }
        intent = Intent(BROADCAST_ACTION)
        handler.removeCallbacks(sendUpdatesToUI)
        handler.postDelayed(sendUpdatesToUI, 1000) // 1 second

    }

    private val sendUpdatesToUI: Runnable = object : Runnable {
        override fun run() {
            DisplayLoggingInfo()
            handler.postDelayed(this, 1000) // 1 seconds
        }
    }

    private fun DisplayLoggingInfo() {
        timeInMilliseconds = SystemClock.uptimeMillis() - initial_time
        updatedTime = timeSwapBuff + timeInMilliseconds

        Log.i("TimerService", "DisplayLoggingInfo:SystemClock ${SystemClock.uptimeMillis()} ")
        Log.i("TimerService", "DisplayLoggingInfo:timeInMilliseconds $timeInMilliseconds ")
        Log.i("TimerService", "DisplayLoggingInfo:updatedTime $updatedTime ")
        val timer = updatedTime.toInt() / 1000
        intent!!.putExtra("timer", updatedTime)
        sendBroadcast(intent)

    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(sendUpdatesToUI)
//        val sharePerfereneces = getSharedPreferences(packageName, Context.MODE_PRIVATE)
//        sharePerfereneces.edit().clear().apply()
    }


    override fun onBind(intent: Intent?): IBinder? {
        Log.i("TimerService", "in onBind()")
        return null
    }


    companion object {
        const val BROADCAST_ACTION = "com.example.traning.MainActivity"
    }
}
