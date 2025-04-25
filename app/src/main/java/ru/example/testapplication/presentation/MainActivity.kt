/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package ru.example.testapplication.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.example.testapplication.Constants
import ru.example.testapplication.MessageReceiverService
import ru.example.testapplication.presentation.theme.TestApplicationTheme

class MainActivity : ComponentActivity() {

    private lateinit var messageClient: MessageClient
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var msg = mutableStateOf("Android")

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        messageClient = Wearable.getMessageClient(this)
        startMessageReceiverService()

        setContent {
            WearApp(msg.value)
        }
    }

    private fun sendData(message: String) {
        coroutineScope.launch {
            try {
                val nodes = Tasks.await(Wearable.getNodeClient(this@MainActivity).connectedNodes)

                for (node in nodes) {
                    val sendMessageTask: Task<Int> =
                        messageClient.sendMessage(node.id, Constants.MESSAGE_PATH, message.toByteArray(Charsets.UTF_8))

                    val result = Tasks.await(sendMessageTask)

                    if (result > 0) {
                        Log.d("MobileDataSender", "Message sent to node successfully")
                    } else {
                        Log.w("MobileDataSender", "Failed to send message to node, result code: $result")
                    }
                }

            } catch (e: Exception) {
                Log.e("MobileDataSender", "Failed to send data", e)
            }
        }
    }

    private fun startMessageReceiverService() {
        val intent = Intent(this, MessageReceiverService::class.java)
        startService(intent)
    }

    @Composable
    fun WearApp(message: String) {
        TestApplicationTheme {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.onBackground,
                        text = message
                    )
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            sendData("Hello from watch OS")
                        }
                    ) {
                        Text("Отправить на МП")
                    }
                }
            }
        }
    }
}