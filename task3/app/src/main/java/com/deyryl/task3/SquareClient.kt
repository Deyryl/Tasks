package com.deyryl.task3

import android.content.Context

interface SquareClient {
    val isBound: Boolean

    fun connect(context: Context)
    fun disconnect(context: Context)
    fun square(number: Int, onResult: (Long) -> Unit)
}