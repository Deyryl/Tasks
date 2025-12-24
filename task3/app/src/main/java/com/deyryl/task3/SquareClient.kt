package com.deyryl.task3

interface SquareClient {
    val isConnected: Boolean

    fun connect()
    fun disconnect()
    fun square(number: Int)
}