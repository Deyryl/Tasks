package com.deyryl.task3

import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import androidx.core.os.bundleOf

class MessengerSquareClient : SquareClient {
    override var isBound: Boolean = false
    private var onResultCb: (Long) -> Unit = {}

    private var serviceMessenger: Messenger? = null
    private var clientMessenger: Messenger = Messenger(ClientHandler {
        onResultCb(it)
    })

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder) {
            serviceMessenger = Messenger(service)
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceMessenger = null
            isBound = false
        }
    }

    private class ClientHandler(private val onResult: (Long) -> Unit) : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                SquareServiceMessenger.MSG_RESULT -> {
                    val result = msg.data.getLong(SquareServiceMessenger.KEY_RESULT)
                    onResult(result)
                } else -> super.handleMessage(msg)
            }
        }
    }

    override fun connect(context: Context) {
        val intent = Intent(context, SquareServiceMessenger::class.java)
        context.bindService(intent, connection, BIND_AUTO_CREATE)
    }

    override fun disconnect(context: Context) {
        if (isBound) context.unbindService(connection)
    }

    override fun square(number: Int, onResult: (Long) -> Unit) {
        if (!isBound) throw IllegalStateException("No bounded service")

        val msg: Message = Message.obtain(null, SquareServiceMessenger.MSG_SQUARE).apply {
            data = bundleOf(SquareServiceMessenger.KEY_NUMBER to number)
            replyTo = clientMessenger
        }
        try {
            serviceMessenger?.send(msg)
            onResultCb = onResult
        } catch (_: RemoteException) {  }
    }
}