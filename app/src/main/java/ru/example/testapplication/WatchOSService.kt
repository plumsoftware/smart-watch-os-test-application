package ru.example.testapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import ru.example.testapp.R

class WatchOSService : Service(), MessageClient.OnMessageReceivedListener {

    private lateinit var messageClient: MessageClient

    override fun onCreate() {
        super.onCreate()
        Log.d("MessageReceiverService", "Service created")
        messageClient = Wearable.getMessageClient(this)
        messageClient.addListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MessageReceiverService", "Service destroyed")
        messageClient.removeListener(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // Мы не используем связь с сервисом
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == Constants.MESSAGE_PATH) {
            val message = String(messageEvent.data)
            Log.d("MessageReceiverService", "Received message: $message")

            // Можно, например, показать уведомление
            showNotification(message)
        }
    }

    private fun showNotification(message: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "wear_os_channel"

        val channel = NotificationChannel(
            channelId,
            "Wear OS Messages",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("New Message")
            .setContentText(message)
            .setSmallIcon(R.drawable.splash_icon)
            .build()

        notificationManager.notify(1, notification)
    }
}