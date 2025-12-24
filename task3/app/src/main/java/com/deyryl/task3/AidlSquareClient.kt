package com.deyryl.task3

import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder

class AidlSquareClient : SquareClient {
    private var squareService: IService? = null
    override var isBound: Boolean = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder) {
            squareService = IService.Stub.asInterface(service)
            if (squareService != null) isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            squareService = null
            isBound = false
        }
    }

    override fun connect(context: Context) {
        val intent = Intent(context, SquareServiceAidl::class.java)
        context.bindService(intent, connection, BIND_AUTO_CREATE)
    }

    override fun disconnect(context: Context) {
        if (isBound) context.unbindService(connection)
    }

    override fun square(number: Int, onResult: (Long) -> Unit) {
        squareService?.square(number, object : IServiceCallback.Stub() {
            override fun onResult(value: Long) {
                onResult(value)
            }
        }) ?: throw IllegalStateException("No service")
    }
}