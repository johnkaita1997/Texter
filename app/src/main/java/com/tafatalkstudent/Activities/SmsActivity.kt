package com.tafatalkstudent.Activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.Telephony
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tafatalkstudent.Shared.Constants.mainScope
import com.tafatalkstudent.Shared.Constants.threadScope
import com.tafatalkstudent.Shared.Contact
import com.tafatalkstudent.Shared.CustomLoadDialogClass
import com.tafatalkstudent.Shared.MyViewModel
import com.tafatalkstudent.Shared.SimCard
import com.tafatalkstudent.Shared.SmsDetail
import com.tafatalkstudent.Shared.goToActivity_Unfinished
import com.tafatalkstudent.Shared.showAlertDialog
import com.tafatalkstudent.databinding.ActivitySmsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class SmsActivity : AppCompatActivity() {

    private var isFirstTime: Boolean = true
    private lateinit var runnable: Runnable
    private lateinit var binding: ActivitySmsBinding
    private val viewmodel: MyViewModel by viewModels()
    private lateinit var adapter: ContactsAdapter
    private lateinit var cdd: CustomLoadDialogClass
    val handler = Handler()
    val delayMillis: Long = 500 // 1 second
    private lateinit var sharedPrefs: SharedPreferences

    companion object {
        val uri = Uri.parse("content://sms")
        val PREFS_NAME = "MyPrefsFile"
        val PREF_FIRST_TIME = "isFirstTime"
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    private fun initall() {

        setUpActiveSimCardIfNotExisting()

        sharedPrefs = getSharedPreferences(PREFS_NAME, 0)
        isFirstTime = sharedPrefs.getBoolean(PREF_FIRST_TIME, true)

        /*
                val SENT = "SMS_SENT"
                val DELIVERED = "SMS_DELIVERED"


                val sentReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        when (resultCode) {
                            Activity.RESULT_OK -> {
                                // Message sent successfully
                                Log.d("Delivery-------", "initall: 00000")
                            }

                            SmsManager.RESULT_ERROR_GENERIC_FAILURE -> {
                                // Generic failure
                                Log.d("Delivery-------", "initall: 111111")
                            }

                            SmsManager.RESULT_ERROR_NO_SERVICE -> {
                                // No service
                                Log.d("Delivery-------", "initall: 2222222")
                            }

                            SmsManager.RESULT_ERROR_NULL_PDU -> {
                                // Null PDU
                                Log.d("Delivery-------", "initall: 33333333")
                            }

                            SmsManager.RESULT_ERROR_RADIO_OFF -> {
                                // Radio off
                                Log.d("Delivery-------", "initall: 444444444")
                            }
                        }
                    }
                }

                val deliveredReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        when (resultCode) {
                            Activity.RESULT_OK -> {
                                // Message delivered successfully
                                Log.d("Delivery-------", "initall: DELIVERED")
                            }
                            Activity.RESULT_CANCELED -> {
                                // Message not delivered
                                Log.d("Delivery-------", "initall: FAILED")
                            }
                        }
                    }
                }

                registerReceiver(sentReceiver, IntentFilter(SENT))
                registerReceiver(deliveredReceiver, IntentFilter(DELIVERED))


                val message = "Test A"
                val mobile = "0725641526"


                val sentPI = PendingIntent.getBroadcast(this, 0, Intent(SENT), PendingIntent.FLAG_IMMUTABLE)
                val deliveredPI = PendingIntent.getBroadcast(this, 0, Intent(DELIVERED), PendingIntent.FLAG_IMMUTABLE)

                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(mobile, null, message, sentPI, deliveredPI)*/

        cdd = CustomLoadDialogClass(this@SmsActivity)
        cdd.setCanceledOnTouchOutside(false)
        cdd.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        adapter = ContactsAdapter(viewmodel, this@SmsActivity, mutableListOf(), mutableListOf())
        val recyclerView: RecyclerView = binding.recyclerviewContacts
        recyclerView.layoutManager = LinearLayoutManager(this@SmsActivity)
        recyclerView.setItemViewCacheSize(10000)
        recyclerView.adapter = adapter

        threadScope.launch {
            saveMessages(this@SmsActivity)
        }


    }


    private fun setUpActiveSimCardIfNotExisting() {
        threadScope.launch {
            viewmodel.insertActiveSimCard(SimCard(0, 1), this@SmsActivity)
        }
    }


    @SuppressLint("Range")
    fun getAllContacts(context: Context): List<Contact> {
        val contactsList = mutableListOf<Contact>()
        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val name =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val phoneNumber =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                contactsList.add(Contact(name, phoneNumber))
            }
        }

        cursor?.close()
        return contactsList
    }


    fun saveMessages(context: Context) {


        if (isFirstTime) {
            mainScope.launch {
                cdd.show()
            }
        } else {
            checkForNewMessages()
            updateSmsList()
        }

        val cursor = context.contentResolver.query(uri, null, null, null, null)
        val numberofItems = cursor!!.count
        cursor.close()

        var standingSmsCount = 0
        threadScope.launch {

            val totalSmsDetailCount = async { viewmodel.getTotalSmsDetailCount(this@SmsActivity) }
            val numberOfDraftItems = async { viewmodel.getDraftSmsCount(this@SmsActivity) }

            standingSmsCount = totalSmsDetailCount.await() - numberOfDraftItems.await()

            val difference = numberofItems - standingSmsCount
            try {
                mainScope.launch {
                    //showAlertDialog("Curosor Count -> ${numberofItems}\nStanding Db Count ->${standingSmsCount}\nDifference -> ${difference}\n")
                }
            } catch (e: Exception) {
                Log.d("-------", "initall: ")
            }

            if (difference > 0) {

                val latestCursor = context.contentResolver.query(uri, null, null, null, "date DESC")
                val deferredList = mutableListOf<Deferred<SmsDetail>>()

                val contactNameMap: MutableMap<String, String> = mutableMapOf()

                latestCursor.use {
                    if (it != null) {
                        if (it.moveToFirst()) {
                            do {
                                val body = it.getString(it.getColumnIndexOrThrow("body"))
                                val phoneNumber = it.getString(it.getColumnIndexOrThrow("address"))
                                val timestamp = it.getLong(it.getColumnIndexOrThrow("date"))
                                val type = it.getInt(it.getColumnIndexOrThrow("type"))
                                val status = it.getInt(it.getColumnIndexOrThrow("status"))
                                val formattedTimestamp = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
                                val deliveryStatus = when (status) {
                                    Telephony.Sms.STATUS_COMPLETE -> "Delivered"
                                    Telephony.Sms.STATUS_FAILED -> "Failed"
                                    else -> "Unknown"
                                }

                                val name = contactNameMap[phoneNumber] ?: getContactName(phoneNumber)
                                contactNameMap[phoneNumber] = name

                                val state = when (type) {
                                    1 -> "Received"  // Inbox
                                    2 -> "Sent"      // Sent messages
                                    3 -> "Draft"     // Draft messages
                                    4 -> "Failed"    // Failed to send messages
                                    5 -> "Queued"    // Queued to send messages
                                    else -> "Unknown"
                                }

                                val deferred = async {
                                    SmsDetail(body, phoneNumber, timestamp, state, status, formattedTimestamp, deliveryStatus, name, true)
                                }
                                deferredList.add(deferred)
                            } while (it.moveToNext())
                        }
                    }
                }

                latestCursor?.close()

                val batchList = deferredList.awaitAll()

                mainScope.launch {
                    try {
                        // showAlertDialog(batchList.size.toString())
                    } catch (e: Exception) {
                        Log.d("-------", "initall: ")
                    }
                }

                // Usage example
                val batchSize = 500
                val batches = mutableListOf<List<SmsDetail>>()

                for (i in 0 until batchList.size step batchSize) {
                    val batch = batchList.subList(i, minOf(i + batchSize, batchList.size))
                    batches.add(batch)
                }

                val _waiter = async {
                    batches.forEach {
                        viewmodel.insertBatchWithRetry(it, this@SmsActivity)
                    }
                }
                _waiter.await()


                mainScope.launch {
                    cdd.dismiss()
                    // Update the shared preferences to indicate that the app has been launched
                    val editor: SharedPreferences.Editor = sharedPrefs.edit()
                    editor.putBoolean(PREF_FIRST_TIME, false)
                    isFirstTime = false
                    editor.apply()
                    updateSmsList()
                }

            } else {
                mainScope.launch {
                    cdd.dismiss()
                    val editor: SharedPreferences.Editor = sharedPrefs.edit()
                    editor.putBoolean(PREF_FIRST_TIME, false)
                    editor.apply()
                    //recreate()
                }
            }

        }


    }


    fun updateSmsList() {

        threadScope.launch {

            val _smslist = async { viewmodel.getLatestSmsList(this@SmsActivity) }
            val smslist = _smslist.await()

            val uniqueSmsMap = mutableMapOf<String, SmsDetail>()

            // Iterate through joinedSmsList and keep only the latest SMS for each unique phone number
            for (smsDetail in smslist) {
                val phoneNumber = smsDetail.phoneNumber.orEmpty()
                if (!uniqueSmsMap.containsKey(phoneNumber) || smsDetail.timestamp!! > (uniqueSmsMap[phoneNumber]?.timestamp ?: 0)) {
                    uniqueSmsMap[phoneNumber] = smsDetail
                }
            }

            val filteredSmsList = uniqueSmsMap.values.toList()

            if (::adapter.isInitialized) {
                mainScope.launch {
                    adapter.setData(filteredSmsList)
                }
            }

        }


    }

    override fun onResume() {
        super.onResume()

        deleExistingMessagesAndUpdate()
        if (!isFirstTime) {
            checkForNewMessages()
        }

    }

    private fun checkForNewMessages() {
        runnable = object : Runnable {
            override fun run() {
                threadScope.launch {

                    val cursor = this@SmsActivity.contentResolver.query(uri, null, null, null, null)
                    val numberofItems = cursor!!.count
                    cursor.close()

                    val totalSmsDetailCount = async { viewmodel.getTotalSmsDetailCount(this@SmsActivity) }
                    val numberOfDraftItems = async { viewmodel.getDraftSmsCount(this@SmsActivity) }

                    val standingSmsCount = totalSmsDetailCount.await() - numberOfDraftItems.await()

                    val difference = numberofItems - standingSmsCount
                    val message = "Curosor Count -> ${numberofItems}\nStanding Db Count ->${standingSmsCount}\nDifference -> ${difference}\n"
                    Log.d("tracker-------", "initall: $message")
                    if (difference > 0) {

                        val batchList = mutableListOf<SmsDetail>()

                        val latestCursor = this@SmsActivity.contentResolver.query(uri, null, null, null, "date DESC LIMIT ${difference}")
                        latestCursor.use {
                            if (it != null) {
                                if (it.moveToFirst()) {
                                    do {

                                        val body = it.getString(it.getColumnIndexOrThrow("body"))
                                        val phoneNumber = it.getString(it.getColumnIndexOrThrow("address"))
                                        val timestamp = it.getLong(it.getColumnIndexOrThrow("date"))
                                        val type = it.getInt(it.getColumnIndexOrThrow("type"))
                                        val status = it.getInt(it.getColumnIndexOrThrow("status"))
                                        val formattedTimestamp = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
                                        val deliveryStatus = when (status) {
                                            Telephony.Sms.STATUS_COMPLETE -> "Delivered"
                                            Telephony.Sms.STATUS_FAILED -> "Failed"
                                            else -> "Unknown"
                                        }
                                        val name = getContactName(phoneNumber)

                                        val state = when (type) {
                                            1 -> "Received"  // Inbox
                                            2 -> "Sent"      // Sent messages
                                            3 -> "Draft"     // Draft messages
                                            4 -> "Failed"    // Failed to send messages
                                            5 -> "Queued"    // Queued to send messages
                                            else -> "Unknown"
                                        }

                                        threadScope.launch {
                                            var isread  = false
                                            isread = state == "Sent"
                                            val smsDetail = SmsDetail(body, phoneNumber, timestamp, state, status, formattedTimestamp, deliveryStatus, name, isread)
                                            batchList.add(smsDetail)
                                            Log.d("herere-------", "initall: Here........................................")
                                        }


                                    } while (it.moveToNext())
                                } else {
                                }
                            } else {
                                mainScope.launch {
                                    //showAlertDialog("Curosor is null")
                                }
                            }
                        }

                        latestCursor?.close()

                        val batchSize = 500
                        val batches = mutableListOf<List<SmsDetail>>()

                        for (i in 0 until batchList.size step batchSize) {
                            val batch = batchList.subList(i, minOf(i + batchSize, batchList.size))
                            batches.add(batch)
                        }


                        threadScope.launch {
                            val _waiter = async {
                                batches.forEach {
                                    viewmodel.insertBatchWithRetry(it, this@SmsActivity)
                                }
                            }
                            _waiter.await()
                        }

                        try {
                            mainScope.launch {
                                updateSmsList()
                            }
                        } catch (e: Exception) {
                            Log.d("-------", "initall: ")
                        }

                    }


                }
                // Call the handler.postDelayed method again to run this code after 1 second
                handler.postDelayed(this, delayMillis)
            }
        }
        // Call the runnable for the first time
        handler.postDelayed(runnable, delayMillis)
    }

    private fun deleExistingMessagesAndUpdate() {
        threadScope.launch {
            val delete = async { viewmodel.deleteMessagesWithPattern(this@SmsActivity) }
            delete.await()
            updateSmsList()
        }
    }


    fun getContactName(phoneNumber: String): String {
        // Try the original number
        var contactName = getContactFromDatabase(phoneNumber)

        // If not found, try with the +254 prefix
        if (contactName.isBlank() && phoneNumber.startsWith("0")) {
            val formattedNumber = "+254" + phoneNumber.substring(1)
            contactName = getContactFromDatabase(formattedNumber)
        }

        // If still not found, try without leading 0 or +
        if (contactName.isBlank() && (phoneNumber.startsWith("+254") || phoneNumber.startsWith("0"))) {
            val formattedNumber = if (phoneNumber.startsWith("+254")) {
                phoneNumber.substring(1)
            } else {
                phoneNumber.substring(1)
            }
            contactName = getContactFromDatabase(formattedNumber)
        }

        // If no contact name found, return the original number
        return if (contactName.isBlank()) {
            phoneNumber
        } else {
            contactName
        }
    }


    fun getContactFromDatabase(phoneNumber: String): String {
        val projection = arrayOf(Phone.DISPLAY_NAME)
        val cursor = contentResolver.query(
            Phone.CONTENT_URI,
            projection,
            Phone.NUMBER + " = ?",
            arrayOf(phoneNumber),
            null
        )

        var contactName = ""
        cursor?.use {
            if (it.moveToFirst()) {
                contactName = it.getString(it.getColumnIndexOrThrow(Phone.DISPLAY_NAME))
            }
        }
        cursor?.close()

        return contactName
    }


    override fun onPause() {
        super.onPause()
        if (::runnable.isInitialized) {
            handler.removeCallbacks(runnable)
        }
    }

    override fun onStop() {
        super.onStop()
        if (::runnable.isInitialized) {
            handler.removeCallbacks(runnable)
        }
    }






}