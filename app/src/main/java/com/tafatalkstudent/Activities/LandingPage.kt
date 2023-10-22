package com.tafatalkstudent.Activities

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony.Sms
import android.telephony.SmsManager
import android.util.Log
import androidx.activity.viewModels
import com.tafatalkstudent.R
import com.tafatalkstudent.Shared.Constants
import com.tafatalkstudent.Shared.Constants.mainScope
import com.tafatalkstudent.Shared.Constants.threadScope
import com.tafatalkstudent.Shared.GroupSmsDetail
import com.tafatalkstudent.Shared.MyViewModel
import com.tafatalkstudent.Shared.SimCard
import com.tafatalkstudent.Shared.formatPhoneNumber
import com.tafatalkstudent.Shared.goToActivity
import com.tafatalkstudent.Shared.goToActivity_Unfinished
import com.tafatalkstudent.Shared.showAlertDialog
import com.tafatalkstudent.databinding.ActivityLandingPageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class LandingPage : AppCompatActivity() {

    private lateinit var binding: ActivityLandingPageBinding
    private val viewmodel: MyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("ActivityName", "Current Activity: " + javaClass.simpleName)
        initall()
    }

    private fun initall() {
        setUpActiveSimCardIfNotExisting()
        resendFailedGroupMessagesIfExisting()
        onClicklisteners()
    }

    private fun resendFailedGroupMessagesIfExisting() {
        threadScope.launch {
            val _failedMessages = async { viewmodel.getFailedGroupMessages(this@LandingPage) }
            val failedMessages = _failedMessages.await()
            failedMessages.forEach {
                sendMessageAndDelete(it)
            }
        }
    }


    private fun onClicklisteners() {
        binding.viewGroups.setOnClickListener {
            goToActivity_Unfinished(this, ViewGroupsActivity::class.java)
        }
        binding.viewMessages.setOnClickListener {
            goToActivity_Unfinished(this, SmsActivity::class.java)
        }
        binding.sendBulkSmsButton.setOnClickListener {
            goToActivity_Unfinished(this, ViewGroupsActivity::class.java)
        }
    }

    private fun setUpActiveSimCardIfNotExisting() {
        GlobalScope.launch {
            viewmodel.insertActiveSimCard(SimCard(0, 1), this@LandingPage)
        }
    }


    private fun sendMessageAndDelete(it: GroupSmsDetail) {
        val phoneNumber = it.phoneNumber
        val message = it.body
        val groupId = it.groupId
        val groupName = it.groupName
        val senderNumber = it.senderNumber
        val senderName = it.senderName
        val codeStamp = it.codeStamp
        val oldTimestamp = it.timestamp
        val formattedTimestamp = it.formattedTimestamp

        val SENT = "SMS_SENT"
        val DELIVERED = "SMS_DELIVERED"

        val sentReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {

                if (resultCode == RESULT_OK) {
                    GlobalScope.launch {
                        viewmodel.deleteGroupMessageByTimestamp(oldTimestamp!!, this@LandingPage)
                    }
                    context?.unregisterReceiver(this)
                }
            }
        }

        val sentPI = PendingIntent.getBroadcast(this, 0, Intent(SENT), PendingIntent.FLAG_IMMUTABLE)
        val deliveredPI = PendingIntent.getBroadcast(this, 0, Intent(DELIVERED), PendingIntent.FLAG_IMMUTABLE)

        if (formatPhoneNumber(senderNumber.toString()) != formatPhoneNumber(phoneNumber.toString())) {
            Log.d("same-------", "initall: Not the same")
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI)
            registerReceiver(sentReceiver, IntentFilter(SENT))
        } else {
            Log.d("same-------", "initall: The same")
        }

        threadScope.launch { delay(1000) }

    }


}