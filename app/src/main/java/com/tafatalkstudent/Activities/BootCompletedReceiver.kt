package com.tafatalkstudent.Activities

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat

class BootCompletedReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("BootCompletedReceiver--", "Received BOOT_COMPLETED broadcast")
        try {
            val serviceIntent = Intent(context, ScheduledService::class.java)
            ContextCompat.startForegroundService(context, serviceIntent)
            Log.d("BootCompletedReceiver--", "initall: Service Started")
        } catch (e: Exception) {
            Log.d("BootCompletedReceiver--", "initall: Service Failed to start ${e.message}")
            //Log.d("BootCompletedReceiver--", "initall: Service Failed to start ${e.message}")
        }
        /*Log.d("boot_broadcast_poc", "starting service...")
        context.startService(Intent(context, ScheduledService::class.java))*/
    }
}
