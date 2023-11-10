package com.tafatalkstudent.Activities

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.util.Log
import android.widget.Switch
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.tafatalkstudent.Shared.Constants.mainScope
import com.tafatalkstudent.Shared.Constants.threadScope
import com.tafatalkstudent.Shared.GetScheduledSmsItem
import com.tafatalkstudent.Shared.GroupSmsDetail
import com.tafatalkstudent.Shared.Groups
import com.tafatalkstudent.Shared.MyViewModel
import com.tafatalkstudent.Shared.PutScheduleSms
import com.tafatalkstudent.Shared.SessionManager
import com.tafatalkstudent.Shared.SimCard
import com.tafatalkstudent.Shared.SmsDetail
import com.tafatalkstudent.Shared.formatPhoneNumber
import com.tafatalkstudent.Shared.goToActivity
import com.tafatalkstudent.Shared.goToActivity_Unfinished
import com.tafatalkstudent.databinding.ActivityLandingPageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LandingPage : AppCompatActivity() {

    private lateinit var binding: ActivityLandingPageBinding
    private val viewmodel: MyViewModel by viewModels()

    companion object {
        var canSynch = false
        var switch: Switch? = null
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceive(context: Context?, intent: Intent?) {
            // Check if the received action matches the one sent from ScheduledService
            if (intent?.action == ScheduledService.ACTION_LAUNCH_OBSERVERS) {
                // Call your function or launch SMS here
                launchObservers()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingPageBinding.inflate(layoutInflater)
        switch = binding.canSynchButton
        setContentView(binding.root)
        Log.d("ActivityName", "Current Activity: " + javaClass.simpleName)
        initall()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initall() {

        fetchSychStatus()

        registerReceiver()
        makeObservations()
        setUpActiveSimCardIfNotExisting()
        resendFailedGroupMessagesIfExisting()
        onClicklisteners()

        //viewmodel.getScheduledSmsData(this@LandingPage)

        fun convertToJson(groups: List<Groups>): String {
            val gson = Gson()
            return gson.toJson(groups)
        }

        fun convertToJson(groups: List<GroupSmsDetail>): String {
            val gson = Gson()
            return gson.toJson(groups)
        }

        fun convertToJson(groups: List<SmsDetail>): String {
            val gson = Gson()
            return gson.toJson(groups)
        }

        threadScope.launch {
            val groups = viewmodel.getAllGroups(this@LandingPage)
            val allGroupSmsDetails = viewmodel.getAllGroupSmsDetails(this@LandingPage)
            val allSmsDetails = viewmodel.getAllSmsDetails(this@LandingPage)
            Log.d("convertToJson-------", "initall: ${convertToJson(groups)}")
            Log.d("convertToJson-------", "initall: ${convertToJson(allGroupSmsDetails)}")
            Log.d("convertToJson-------", "initall: ${convertToJson(allSmsDetails)}")
        }

        val intent = Intent(this, ScheduledService::class.java)
        startService(intent)

    }

    private fun fetchSychStatus() {
        threadScope.launch {
            val allowedSynch = async { viewmodel.getSynchPermit(this@LandingPage) }.await()
            allowedSynch?.let {
                canSynch = allowedSynch
                mainScope.launch {
                    binding.canSynchButton.isChecked = it
                }
            }
        }
    }

    private fun registerReceiver() {
        val intentFilter = IntentFilter(ScheduledService.ACTION_LAUNCH_OBSERVERS)
        registerReceiver(broadcastReceiver, intentFilter)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun makeObservations() {
        viewmodel.scheduledSmsData.observe(this) { scheduledSmsData ->
            // Handle changes in scheduledSmsData here
            scheduledSmsData?.let {
                val size = it.size
                if (size > 0) {
                    it.forEachIndexed { index, getScheduledSmsItem ->
                        tryToSendMessage(getScheduledSmsItem)
                    }
                }
            }

        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun tryToSendMessage(it: GetScheduledSmsItem) {

        val subscriptionManager: SubscriptionManager
        val activeSubscriptionInfoList: MutableList<SubscriptionInfo>
        subscriptionManager = getSystemService(SubscriptionManager::class.java)
        activeSubscriptionInfoList = subscriptionManager.activeSubscriptionInfoList
        val isdualSim = activeSubscriptionInfoList.size >= 2

        val message = it.body
        val phoneNumbers = it.to
        val type = it.type
        val groupId = it.groupId
        val sentid = it.id

        val SENT = "SMS_SENT"
        val DELIVERED = "SMS_DELIVERED"


        phoneNumbers?.forEachIndexed { index, mobile ->

            var sentReceiver: BroadcastReceiver? = null

            sentReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (resultCode == RESULT_OK) {
                        GlobalScope.launch {
                            try {
                                context?.unregisterReceiver(sentReceiver)
                            } catch (e: Exception) {
                                context?.unregisterReceiver(sentReceiver)
                            }

                            val updateObject = PutScheduleSms(phoneNumbers, message, groupId, type, sentid)
                            threadScope.launch {
                                viewmodel.updateScheduledSms(updateObject, this@LandingPage)
                            }
                        }
                    } else {
                        GlobalScope.launch {
                            try {
                                context?.unregisterReceiver(sentReceiver)
                            } catch (e: Exception) {
                                context?.unregisterReceiver(sentReceiver)
                            }
                        }
                    }
                }
            }

            val sentPI = PendingIntent.getBroadcast(this, 0, Intent(SENT), PendingIntent.FLAG_IMMUTABLE)
            val deliveredPI = PendingIntent.getBroadcast(this, 0, Intent(DELIVERED), PendingIntent.FLAG_IMMUTABLE)

            if (isdualSim == false) {
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(mobile, null, message, sentPI, deliveredPI)
                registerReceiver(sentReceiver, IntentFilter(SENT))
            } else {
                mainScope.launch {
                    val theselectedSim = viewmodel.getActiveSimCard(this@LandingPage)!!.body
                    val smsManager = SmsManager.getSmsManagerForSubscriptionId(theselectedSim!!)
                    smsManager.sendTextMessage(mobile, null, message, sentPI, deliveredPI)
                    registerReceiver(sentReceiver, IntentFilter(SENT))
                }
            }

        }

    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun launchObservers() {
        threadScope.launch {
            val _schedule = async { viewmodel.getScheduledSmsDataService(this@LandingPage) }
            val schedule = _schedule.await()

            schedule?.let {
                val size = schedule.size
                if (size > 0) {
                    schedule.forEachIndexed { index, getScheduledSmsItem ->
                        tryToSendMessage(getScheduledSmsItem)
                    }
                }
            }

        }
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

        binding.logout.setOnClickListener {
            val logout = SessionManager(this).logout()
            if (logout) {
                goToActivity(this@LandingPage, LoginActivity::class.java)
            }
        }

        binding.viewGroups.setOnClickListener {
            goToActivity_Unfinished(this, ViewGroupsActivity::class.java)
        }
        binding.viewMessages.setOnClickListener {
            goToActivity_Unfinished(this, SmsActivity::class.java)
        }
        binding.sendBulkSmsButton.setOnClickListener {
            goToActivity_Unfinished(this, ViewGroupsActivity::class.java)
        }

        binding.canSynchButton.setOnCheckedChangeListener { _, isChecked ->

            if (canSynch != isChecked) {
                if (isChecked) {
                    threadScope.launch {
                        viewmodel.postSynchPermit(binding.canSynchButton, true, this@LandingPage)
                    }
                } else {
                    threadScope.launch {
                        viewmodel.postSynchPermit(binding.canSynchButton, false, this@LandingPage)
                    }
                }
            }
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