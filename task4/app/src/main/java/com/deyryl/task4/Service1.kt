package com.deyryl.task4

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Process
import android.widget.Toast

class Service1 : Service() {
    private var serviceHandler: ServiceHandler? = null

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_SERVICE -> {
                    Thread.sleep(5000)
                    startForegroundService(Intent(this@Service1, Service2::class.java))
                    stopSelf(msg.arg1)
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    override fun onCreate() {
        HandlerThread("FirstService", Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()
            serviceHandler = ServiceHandler(looper)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()
        serviceHandler?.obtainMessage()?.also { msg ->
            msg.what = MSG_SERVICE
            msg.arg1 = startId
            serviceHandler?.sendMessage(msg)
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val MSG_SERVICE = 1
    }
}