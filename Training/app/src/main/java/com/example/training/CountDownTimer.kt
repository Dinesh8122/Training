package com.example.training

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_count_down_timer.*

class CountDownTimer : AppCompatActivity(), View.OnClickListener {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count_down_timer)

        setButtonLinser()
    }
    fun setButtonLinser(){
        start_button.setOnClickListener(this)
        stop_button.setOnClickListener(this)
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                updateGUI(intent)

            }
        }
    }


    override fun onClick(v: View?) {
        when(v){
            start_button ->{
                val intent = Intent(this, CountDownTimerService::class.java)
                startService(intent)
                registerReceiver(broadcastReceiver, IntentFilter(CountDownTimerService.COUNTDOWN_BR))

            }
            stop_button ->{
                val intent = Intent(this, CountDownTimerService::class.java)
                unregisterReceiver(broadcastReceiver);
                stopService(intent)
            }
        }
    }

    private fun updateGUI(intent: Intent) {
        if (intent.extras != null) {
            val millisUntilFinished = intent.getLongExtra("countdown", 30000)
            Log.i("MainActivity", "Countdown seconds remaining:" + millisUntilFinished / 1000)
            val time = millisUntilFinished / 1000
            timer_display.text=time.toString()
//            txt.setText(java.lang.Long.toString(millisUntilFinished / 1000))
            val sharedPreferences = getSharedPreferences(packageName, MODE_PRIVATE)
            sharedPreferences.edit().putLong("countdown", millisUntilFinished).apply()
        }
    }

    override fun onResume() {
        super.onResume()


        registerReceiver(broadcastReceiver, IntentFilter(CountDownTimerService.COUNTDOWN_BR))

    }

    override fun onStart() {
        super.onStart()
        registerReceiver(broadcastReceiver, IntentFilter(CountDownTimerService.COUNTDOWN_BR))

    }

}