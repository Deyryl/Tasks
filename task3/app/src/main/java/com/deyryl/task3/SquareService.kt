package com.deyryl.task3

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import androidx.core.os.bundleOf

class SquareService : Service() {

    private val messenger = Messenger(IncomingHandler())

    private class IncomingHandler : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MSG_SQUARE -> {
                        val number = msg.data.getInt(KEY_NUMBER)
                        val result = square(number)

                        val reply = Message.obtain(null, MSG_RESULT).apply {
                            data = bundleOf(KEY_RESULT to result)
                        }
                        try {
                            msg.replyTo.send(reply)
                        } catch (_: RemoteException) { }
                    }
                    else -> super.handleMessage(msg)
                }
            }
    }

    override fun onBind(intent: Intent?): IBinder? = messenger.binder

    companion object {
        const val MSG_SQUARE = 0
        const val MSG_RESULT = 1

        const val KEY_NUMBER = "number"
        const val KEY_RESULT = "result"

        fun square(number: Int): Long = number.toLong() * number
    }
}