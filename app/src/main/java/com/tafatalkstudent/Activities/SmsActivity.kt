package com.tafatalkstudent.Activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Telephony
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tafatalkstudent.Shared.Constants.mainScope
import com.tafatalkstudent.Shared.Constants.threadScope
import com.tafatalkstudent.Shared.CustomLoadDialogClass
import com.tafatalkstudent.Shared.MyViewModel
import com.tafatalkstudent.Shared.RoomDb
import com.tafatalkstudent.Shared.SmsDetail
import com.tafatalkstudent.Shared.makeLongToast
import com.tafatalkstudent.Shared.showAlertDialog
import com.tafatalkstudent.databinding.ActivitySmsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class SmsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySmsBinding
    private val viewmodel: MyViewModel by viewModels()
    private lateinit var adapter: ContactsAdapter

    private lateinit var cdd: CustomLoadDialogClass

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

        threadScope.launch {
            saveMessages(this@SmsActivity)
        }


    }


    private fun fetchSMS() {

        threadScope.launch {

            val database = RoomDb(this@SmsActivity).getSmsDao()
            val newSmsList = database.getNewLatestSmsList()
            val modifiedSmsList = newSmsList.map { smsDetail ->
                smsDetail.copy(
                    phoneNumber = smsDetail.phoneNumber?.replace("^\\+254".toRegex(), "0")
                )
            }
            val uniqueModifiedSmsMap = mutableMapOf<String, SmsDetail>()
            modifiedSmsList.sortedByDescending { it.timestamp }.forEach { smsDetail ->
                val phoneNumber = smsDetail.phoneNumber.orEmpty()
                if (!uniqueModifiedSmsMap.containsKey(phoneNumber)) {
                    uniqueModifiedSmsMap[phoneNumber] = smsDetail
                }
            }
            val thenewlist = uniqueModifiedSmsMap.values.toList()

            val recyclerView: RecyclerView = binding.recyclerviewContacts
            mainScope.launch {
                adapter = ContactsAdapter(viewmodel, this@SmsActivity, mutableListOf(), thenewlist)
                recyclerView.layoutManager = LinearLayoutManager(this@SmsActivity)
                recyclerView.adapter = adapter
            }

            updateSmsList()

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


    fun saveMessages(context: Context): List<SmsMessage> {


        val PREFS_NAME = "MyPrefsFile"
        val PREF_FIRST_TIME = "isFirstTime"

        val sharedPrefs: SharedPreferences = getSharedPreferences(PREFS_NAME, 0)
        val isFirstTime = sharedPrefs.getBoolean(PREF_FIRST_TIME, true)

        if (isFirstTime) {
            mainScope.launch {
                cdd.show()
            }
        } else {
            fetchSMS()
        }

        val smsList = mutableListOf<SmsMessage>()
        val uri = Uri.parse("content://sms")
        val cursor = context.contentResolver.query(uri, null, null, null, null)

        val batchList = mutableListOf<SmsDetail>()

        cursor?.use {
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
                    val state = when (type) {
                        1 -> "Received"  // Inbox
                        2 -> "Sent"      // Sent messages
                        3 -> "Draft"     // Draft messages
                        4 -> "Failed"    // Failed to send messages
                        5 -> "Queued"    // Queued to send messages
                        else -> "Unknown"
                    }

                    threadScope.launch {
                        val doesBodyStateMessageExistInNewDB = viewmodel.doesBodyStateMessageExistInNewDB(body, state, this@SmsActivity)
                        val exists = doesBodyStateMessageExistInNewDB
                        if (!exists) {
                            val smsDetail = SmsDetail(body, phoneNumber, timestamp, state, status, formattedTimestamp, deliveryStatus)
                            batchList.add(smsDetail)
                            /*viewmodel.insertSmsDetail(smsDetail, this@SmsActivity, "old")*/
                            Log.d("herere-------", "initall: Here........................................")
                        }
                    }


                } while (it.moveToNext())
            }
        }


        val batchSize = 500
        val batches = mutableListOf<List<SmsDetail>>()

        for (i in 0 until batchList.size step batchSize) {
            val batch = batchList.subList(i, minOf(i + batchSize, batchList.size))
            batches.add(batch)
        }

        batches.forEach {
            threadScope.launch {
                viewmodel.insertBatch(it, this@SmsActivity)
            }
        }

        mainScope.launch {
            delay(3000)
        }

        mainScope.launch {
            cdd.dismiss()
            // Update the shared preferences to indicate that the app has been launched
            val editor: SharedPreferences.Editor = sharedPrefs.edit()
            editor.putBoolean(PREF_FIRST_TIME, false)
            editor.apply()
            updateSmsList()
        }

        cursor?.close()
        return smsList
    }


    fun updateSmsList() {

        threadScope.launch {

            val database = RoomDb(this@SmsActivity).getSmsDao()
            val newSmsList = database.getNewLatestSmsList()
            val modifiedSmsList = newSmsList.map { smsDetail ->
                smsDetail.copy(
                    phoneNumber = smsDetail.phoneNumber?.replace("^\\+254".toRegex(), "0")
                )
            }
            val uniqueModifiedSmsMap = mutableMapOf<String, SmsDetail>()
            modifiedSmsList.sortedByDescending { it.timestamp }.forEach { smsDetail ->
                val phoneNumber = smsDetail.phoneNumber.orEmpty()
                if (!uniqueModifiedSmsMap.containsKey(phoneNumber)) {
                    uniqueModifiedSmsMap[phoneNumber] = smsDetail
                }
            }
            val thenewlist = uniqueModifiedSmsMap.values.toList()
            Log.d("-------", "initall: ${thenewlist.toString()}")
            if (::adapter.isInitialized) {
                adapter.setData(thenewlist)
            }


            val _smslist = async { viewmodel.getLatestSmsList(this@SmsActivity) }
            val smslist = _smslist.await()


            val joinedSmsList = thenewlist + smslist
            if (::adapter.isInitialized) {
                adapter.setData(joinedSmsList)
            }


        }


    }

    override fun onResume() {
        super.onResume()
        updateSmsList()
    }

}