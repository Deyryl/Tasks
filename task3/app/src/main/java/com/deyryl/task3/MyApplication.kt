package com.deyryl.task3

import android.app.Application

class MyApplication : Application() {
    val appContainer = AppContainer()
}

class AppContainer {
    val squareClient = MessengerSquareClient()
}