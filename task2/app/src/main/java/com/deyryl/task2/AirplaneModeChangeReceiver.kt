package com.deyryl.task2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AirplaneModeChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val isAirplaneModeEnabled = intent?.getBooleanExtra(AirPlaneTag, false)
            ?: return

        Log.d("AirplaneMode", "$isAirplaneModeEnabled")
    }

    companion object {
        const val AirPlaneTag = "state"
    }
}