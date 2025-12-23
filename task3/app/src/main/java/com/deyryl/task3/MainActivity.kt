package com.deyryl.task3

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf

class MainActivity : ComponentActivity() {
    private var resultState = mutableStateOf("")

    private var bound = false

    private var serviceMessenger: Messenger? = null
    private var clientMessenger: Messenger = Messenger(ClientHandler {
        resultState.value = it.toString()
    })

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder) {
            serviceMessenger = Messenger(service)
            bound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceMessenger = null
            bound = false
        }
    }

    private class ClientHandler(private val onResult: (Long) -> Unit) : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                SquareService.MSG_RESULT -> {
                    val result = msg.data.getLong(SquareService.KEY_RESULT)
                    onResult(result)
                } else -> {
                    super.handleMessage(msg)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MainScreen(resultState.value, Modifier.fillMaxSize()) { number ->
                numberToSquare(number)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, SquareService::class.java).also { intent ->
            bindService(intent, connection, BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (bound) unbindService(connection)
    }

    private fun numberToSquare(number: Int) {
        if (!bound) throw RuntimeException("No bounded service")

        val msg: Message = Message.obtain(null, SquareService.MSG_SQUARE).apply {
            data = bundleOf(SquareService.KEY_NUMBER to number)
            replyTo = clientMessenger
        }
        try {
            serviceMessenger?.send(msg)
        } catch (_: RemoteException) {

        }
    }
}

@Composable
fun MainScreen(
    resultText: String,
    modifier: Modifier = Modifier,
    onRequestSquare: (Int) -> Unit
) {
    var number by remember { mutableStateOf("") }

    var showResult by remember { mutableStateOf(false) }
    val maxSymbols = 8

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = number,
            onValueChange = {
                if (it.length <= maxSymbols) {
                    number = it
                }
                showResult = false
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            keyboardActions = KeyboardActions(
                onDone = {
                    if (number.toIntOrNull() != null) {
                        onRequestSquare(number.toInt())
                        showResult = true
                    }
                }

            )
        )
        Spacer(Modifier.height(20.dp))
        if (showResult && resultText.isNotBlank()) {
            Text(
                text = "Square of $number is $resultText"
            )
        }
    }
}