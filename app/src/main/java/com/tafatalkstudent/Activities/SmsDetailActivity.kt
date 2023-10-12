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
import android.telephony.SubscriptionManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tafatalkstudent.Shared.Constants.mainScope
import com.tafatalkstudent.Shared.Constants.threadScope
import com.tafatalkstudent.Shared.MyViewModel
import com.tafatalkstudent.Shared.SmsDetail
import com.tafatalkstudent.Shared.showAlertDialog
import com.tafatalkstudent.databinding.ActivitySmsDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class SmsDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySmsDetailBinding
    private lateinit var phoneNumber: String
    private val viewmodel: MyViewModel by viewModels()
    private lateinit var adapter: SmsDetailAdapter

    companion object {
        var timestamp: Long = 0
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun initall() {

        timestamp = System.nanoTime()
        val formattedTimestamp = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))

        phoneNumber = intent.getStringExtra("phoneNumber").toString()

        threadScope.launch {

            val newphoneNumberMessageList = async { viewmodel.getMessagesByPhoneNumber(phoneNumber, this@SmsDetailActivity, "new") }

            val recyclerView: RecyclerView = binding.recyclerViewMessageDetails

            mainScope.launch {
                adapter = SmsDetailAdapter(viewmodel, this@SmsDetailActivity, newphoneNumberMessageList.await(), binding.etMessage)
                recyclerView.layoutManager = LinearLayoutManager(this@SmsDetailActivity)

                /*val viewPool = RecyclerView.RecycledViewPool()
                recyclerView.setRecycledViewPool(viewPool)
                recyclerView.setItemViewCacheSize(1000)*/

                recyclerView.adapter = adapter
                val lastItemIndex = adapter.itemCount.minus(-1)
                recyclerView.scrollToPosition(lastItemIndex)
            }

            val oldphoneNumberMessageList = async { viewmodel.getMessagesByPhoneNumber(phoneNumber, this@SmsDetailActivity, "old") }.await()
            val joinedlist: List<SmsDetail> = newphoneNumberMessageList.await() + oldphoneNumberMessageList
            adapter.setData(joinedlist)

        }





        binding.etMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val newText = s.toString()

                if (newText.isEmpty()) {
                    threadScope.launch {
                        viewmodel.deleteMessageByTimestamp(timestamp, this@SmsDetailActivity)
                    }
                } else {
                    threadScope.launch {
                        delay(100)
                        viewmodel.insertSmsDetail(SmsDetail(newText, phoneNumber, timestamp, "Draft", 3, formattedTimestamp, "Unsent"), this@SmsDetailActivity, "new")
                    }
                }

            }
        })











        binding.submitSMS.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()
            if (binding.etMessage.text!!.isEmpty()) {
                binding.etMessage.setError("Enter Message")
            } else {

                threadScope.launch {

                    val _exists = async { viewmodel.doesMessageExist(timestamp, this@SmsDetailActivity, "new") }

                    val subscriptionManager = getSystemService(SubscriptionManager::class.java)
                    val activeSubscriptionInfoList = subscriptionManager.activeSubscriptionInfoList
                    val exists = _exists.await()

                    if (activeSubscriptionInfoList != null && activeSubscriptionInfoList.size >= 2) {
                        activeSubscriptionInfoList.map { it.carrierName.toString() }.toTypedArray()

                        mainScope.launch {
                            val simCardPickerDialog = AlertDialog.Builder(this@SmsDetailActivity)
                                .setTitle("Select SIM card")
                                .setItems(arrayOf("SIM 1", "SIM 2")) { _, which ->
                                    // User selected a SIM card
                                    val selectedSimSlot = which + 1 // SIM card slot numbers are 1-based
                                    sendNormalMessage(message, timestamp, phoneNumber, exists, selectedSimSlot)
                                }
                                .create()
                            simCardPickerDialog.show()
                        }


                    } else {
                        // Less than 2 active SIM cards or no SIM card present
                        sendNormalMessage(message, timestamp, phoneNumber, exists, null)
                        // Handle this case accordingly
                    }

                }
            }
        }

    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun sendNormalMessage(message: String, timestamp: Long, toMobile: String, exists: Boolean, selectedSimId: Int?) {

        disableEditTextAndButton()

        val formattedTimestamp = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))

        val SENT = "SMS_SENT"
        val DELIVERED = "SMS_DELIVERED"

        val sentReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (resultCode == RESULT_OK) {
                    threadScope.launch {
                        viewmodel.insertSmsDetail(SmsDetail(message, phoneNumber, timestamp, "Sent", 2, formattedTimestamp, "Sent"), this@SmsDetailActivity, "new")
                        delay(100)
                        updateUI(phoneNumber)
                        enableButton()
                    }
                } else {
                    threadScope.launch {
                        viewmodel.insertSmsDetail(SmsDetail(message, phoneNumber, timestamp, "Failed", 4, formattedTimestamp, "Failed"), this@SmsDetailActivity, "new")
                        delay(100)
                        updateUI(phoneNumber)
                        enableButton()
                    }
                }
            }
        }

        val deliveredReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (resultCode == RESULT_OK) {
                    threadScope.launch {
                        viewmodel.insertSmsDetail(SmsDetail(message, phoneNumber, timestamp, "Delivered", 1, formattedTimestamp, "Delivered"), this@SmsDetailActivity, "new")
                        updateUI(phoneNumber)
                        enableButton()
                    }
                } else {
                    threadScope.launch {
                        viewmodel.insertSmsDetail(SmsDetail(message, phoneNumber, timestamp, "Failed", 4, formattedTimestamp, "Failed"), this@SmsDetailActivity, "new")
                        updateUI(phoneNumber)
                        enableButton()
                    }
                }
            }
        }


        val sentPI = PendingIntent.getBroadcast(this, 0, Intent(SENT), PendingIntent.FLAG_IMMUTABLE)
        val deliveredPI = PendingIntent.getBroadcast(this, 0, Intent(DELIVERED), PendingIntent.FLAG_IMMUTABLE)


        if (selectedSimId == null) {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(toMobile, null, message, sentPI, deliveredPI)
            registerReceiver(sentReceiver, IntentFilter(SENT))
            registerReceiver(deliveredReceiver, IntentFilter(DELIVERED))
        } else {
            val smsManager = SmsManager.getSmsManagerForSubscriptionId(selectedSimId)
            smsManager.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI)
            // Register the broadcast receivers
            registerReceiver(sentReceiver, IntentFilter(SENT))
            registerReceiver(deliveredReceiver, IntentFilter(DELIVERED))
        }


    }

    private fun disableEditTextAndButton() {
        mainScope.launch {
            binding.etMessage.setText("")
            binding.submitSMS.isEnabled = false
        }
    }

    private fun updateUI(phoneNumber: String) {
        threadScope.launch {
            val newphoneNumberMessageList = async { viewmodel.getMessagesByPhoneNumber(phoneNumber, this@SmsDetailActivity, "new") }
            val oldphoneNumberMessageList = async { viewmodel.getMessagesByPhoneNumber(phoneNumber, this@SmsDetailActivity, "old") }.await()
            val joinedlist: List<SmsDetail> = newphoneNumberMessageList.await() + oldphoneNumberMessageList
            adapter.setData(joinedlist)
        }
    }


    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun sendSMSUsingSim(selectedSimId: Int, toMobile: String, message: String, exists: Boolean) {
        try {
            val method = Class.forName("android.telephony.SubscriptionManager").getDeclaredMethod("getSubId", Int::class.javaPrimitiveType)
            method.isAccessible = true
            val param = method.invoke(null, selectedSimId) as? IntArray
            if (param != null && param.isNotEmpty()) {
                val inst: Int = param[0]
                val smsMan = SmsManager.getSmsManagerForSubscriptionId(inst)
                smsMan.sendTextMessage(toMobile, null, message, null, null)
            } else {
                // Fallback option: Try to get subscription ID using SubscriptionManager.fromContext
                val subscriptionManager = SubscriptionManager.from(this)
                val subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(selectedSimId)
                if (subscriptionInfo != null) {
                    val inst = subscriptionInfo.subscriptionId
                    val smsMan = SmsManager.getSmsManagerForSubscriptionId(inst)
                    smsMan.sendTextMessage(toMobile, null, message, null, null)
                } else {
                    val newsimid = selectedSimId - 2
                    sendSMSUsingSim(newsimid, toMobile, message, exists)
                }
            }
        } catch (e: Exception) {
            showAlertDialog(e.message ?: "Unknown error occurred.")
        }
    }


    private fun enableButton() {
        mainScope.launch {
            binding.submitSMS.isEnabled = true
        }
    }

}