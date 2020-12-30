package com.example.training

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable

class CountDownTimerService : Service() {

        private val TAG = "BroadcastService"
        var intent: Intent = Intent(CountDownTimerService.Companion.COUNTDOWN_BR)
        lateinit var countDownTimer: CountDownTimer
        lateinit var sharedPreferences: SharedPreferences

        override fun onCreate() {
            super.onCreate()
            Log.i(TAG, "Starting timer...")
            sharedPreferences = getSharedPreferences(packageName,MODE_PRIVATE);
            var millis = sharedPreferences.getLong("countdown", 30000)
            if (millis / 1000 == 0L) {
                millis = 30000
            }
            countDownTimer = object : CountDownTimer(millis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    Log.i(TAG, "Countdown seconds remaining:" + millisUntilFinished / 1000)
                    intent.putExtra("countdown", millisUntilFinished)
                    sendBroadcast(intent)
                }

                override fun onFinish() {
                    onDestroy()
                }
            }
            countDownTimer.start()
        }

        override fun onDestroy() {
            countDownTimer.cancel()
            sharedPreferences.edit().remove("countdown").apply()
            super.onDestroy()
        }

        @Nullable
        override fun onBind(intent: Intent?): IBinder? {
            return null
        }

    companion object {
        private const val TAG = "BroadcastService"
        const val COUNTDOWN_BR = "your_package_name.countdown_br"
    }
}