package com.deyryl.task3

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
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

class MainActivityAidl : ComponentActivity() {
    private var resultState = mutableStateOf("")

    private var bound = false

    private var squareService: IService? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder) {
            squareService = IService.Stub.asInterface(service)
            Log.d("service", "$squareService")
            bound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            squareService = null
            bound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MainScreen(resultState.value, Modifier.fillMaxSize()) { number ->
                Log.d("service", "$squareService")
                resultState.value = (squareService?.square(number) ?: 0).toString()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, SquareServiceAidl::class.java).also { intent ->
            bindService(intent, connection, BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (bound) unbindService(connection)
    }
}

@Composable
private fun MainScreen(
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