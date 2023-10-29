package com.tafatalkstudent.Activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.tafatalkstudent.R
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class ScheduledService : Service() {

    private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    companion object {
        const val ACTION_LAUNCH_OBSERVERS = "ACTION_LAUNCH_OBSERVERS"
        private const val NOTIFICATION_CHANNEL_ID = "ScheduledServiceChannel"
        private const val NOTIFICATION_ID = 123
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        val notificationIntent = Intent(this, LandingPage::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,     PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Smart Sms")
            .setContentText("Processing sms...")
            .setSmallIcon(R.drawable.logo)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        executor.scheduleAtFixedRate({
            // Execute your code here
            // Send a broadcast to trigger the function in LandingPage
            val launchObserversIntent = Intent(ACTION_LAUNCH_OBSERVERS)
            sendBroadcast(launchObserversIntent)
        }, 0, 10, TimeUnit.SECONDS) // Run every 5 minutes

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Scheduled Service"
            val descriptionText = "Running in the background"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

