package com.deyryl.task2

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.deyryl.task2.ui.theme.Task2Theme

class MainActivity : ComponentActivity() {
    private lateinit var airplaneModeChangeReceiver: AirplaneModeChangeReceiver
    private val intentFilter = IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        airplaneModeChangeReceiver = AirplaneModeChangeReceiver()
        registerReceiver(airplaneModeChangeReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(airplaneModeChangeReceiver)
    }
}