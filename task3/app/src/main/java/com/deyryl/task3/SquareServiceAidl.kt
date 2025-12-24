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

class SquareServiceAidl : Service() {
    val binder = object : IService.Stub() {
        override fun square(number: Int): Long {
            return number.toLong() * number
        }
    }

    override fun onBind(intent: Intent?): IBinder? = binder
}