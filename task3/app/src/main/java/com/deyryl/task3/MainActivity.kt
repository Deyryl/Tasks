package com.deyryl.task3

import android.os.Bundle
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

class MainActivity : ComponentActivity() {
    private val resultState = mutableStateOf("")

    private lateinit var squareClient: SquareClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        squareClient = (application as MyApplication).appContainer.squareClient

        setContent {
            MainScreen(resultState.value, Modifier.fillMaxSize()) { number ->
                squareClient.square(number) {
                    resultState.value = it.toString()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        squareClient.connect(this)
    }

    override fun onStop() {
        super.onStop()
        squareClient.disconnect(this)
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