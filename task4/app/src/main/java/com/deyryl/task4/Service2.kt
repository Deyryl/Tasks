package com.deyryl.task4

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Process
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat

/**
 * В задании написано, что через 40 секунд показать, что сервис будет убит без
 * notification. На моих эмуляторе и устройстве это заняло около 30 секунд, когда
 * тестировал.
 * Также тестировал, что если запустить через startService и без foreground компонентов,
 * то сервис будет жить около минуты, после также уничтожается системой.
 */

class Service2 : Service() {
    private var serviceThread: HandlerThread? = null
    private var serviceHandler: ServiceHandler? = null

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            postDelayed(
                {
                    val intent = Intent().apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        setComponent(ComponentName("com.deyryl.task3", "com.deyryl.task3.MainActivity"))
                    }
                    startActivity(intent)
                },
                MILLISECONDS
            )
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        val nm = getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Service channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        nm.createNotificationChannel(channel)
        serviceThread = HandlerThread("Second Service", Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()
            serviceHandler = ServiceHandler(looper)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Service running")
            .setContentText("Waiting…")
            .setOngoing(true)
            .build()

        val fgsType =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE
            } else {
                0
            }

        ServiceCompat.startForeground(this, NOTIF_ID, notification, fgsType)

        serviceHandler?.obtainMessage()?.also { msg ->
            msg.arg1 = startId
            serviceHandler?.sendMessage(msg)
        }
        Log.d("service", "service was started")
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        serviceThread?.quitSafely()
        Log.d("Service", "Service 2 was destroyed")
    }

    companion object {
        const val MILLISECONDS = 40_000L
        private const val CHANNEL_ID = "service_channel"
        private const val NOTIF_ID = 100
    }
}