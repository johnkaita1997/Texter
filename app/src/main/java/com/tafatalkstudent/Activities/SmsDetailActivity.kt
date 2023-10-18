package com.tafatalkstudent.Activities

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tafatalkstudent.Shared.Constants.mainScope
import com.tafatalkstudent.Shared.Constants.threadScope
import com.tafatalkstudent.Shared.MyViewModel
import com.tafatalkstudent.Shared.SimCard
import com.tafatalkstudent.Shared.SmsDetail
import com.tafatalkstudent.Shared.makeLongToast
import com.tafatalkstudent.Shared.showAlertDialog
import com.tafatalkstudent.databinding.ActivitySmsDetailBinding
import com.tafatalkstudent.databinding.BottomsheetBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class SmsDetailActivity : AppCompatActivity() {

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var activeSubscriptionInfoList: MutableList<SubscriptionInfo>
    private lateinit var subscriptionManager: SubscriptionManager
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    @SuppressLint("MissingPermission")

    private lateinit var bottomSheetBinding: BottomsheetBinding
    private var phoneNumberStamp = 9999999999999999
    private lateinit var sentReceiver: BroadcastReceiver

    //private lateinit var deliveredReceiver: BroadcastReceiver
    private lateinit var binding: ActivitySmsDetailBinding
    private lateinit var phoneNumber: String
    private val viewmodel: MyViewModel by viewModels()
    private lateinit var adapter: SmsDetailAdapter
    private lateinit var recyclerView: RecyclerView
    private var isUpdating = false // Add this variable to track ongoing updates

    private lateinit var name: String
    private lateinit var isNumericOnly: String
    private lateinit var colorCode: String
    private lateinit var upperCasedName: String

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

        setTextWithActiveSimCardNumber()

        subscriptionManager = getSystemService(SubscriptionManager::class.java)
        activeSubscriptionInfoList = subscriptionManager.activeSubscriptionInfoList

        bottomSheetBinding = BottomsheetBinding.inflate(LayoutInflater.from(this))
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetBinding.sim1.setOnClickListener {
            setActiveSimCard(1)
        }
        bottomSheetBinding.sim2.setOnClickListener {
            setActiveSimCard(2)
        }


        timestamp = System.nanoTime()
        val formattedTimestamp = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
        recyclerView = binding.recyclerViewMessageDetails

        phoneNumber = intent.getStringExtra("phoneNumber").toString()
        name = intent.getStringExtra("name").toString()
        isNumericOnly = intent.getStringExtra("isNumericOnly").toString()
        colorCode = intent.getStringExtra("colorCode").toString()
        upperCasedName = intent.getStringExtra("upperCasedName").toString()
        name = intent.getStringExtra("name").toString()

        if (isNumericOnly.equals("false")) {
            binding.namedIv.setBackgroundColor(Color.parseColor("#9ba0e4"))
            binding.namedTv.text = upperCasedName
        } else {
            binding.unnamedRL.visibility = View.VISIBLE
            binding.namedRL.visibility = View.GONE
        }

        binding.textViewNameOfPerson.setText(name)

        adapter = SmsDetailAdapter(viewmodel, this@SmsDetailActivity, binding.etMessage)
        recyclerView.layoutManager = LinearLayoutManager(this@SmsDetailActivity)
        /*val viewPool = RecyclerView.RecycledViewPool()
        recyclerView.setRecycledViewPool(viewPool)*/
        recyclerView.setItemViewCacheSize(1000)
        recyclerView.adapter = adapter

        populateMessageRecyclerView()

        loadDraftMessageToEditText()

        onClickListeners(formattedTimestamp)

    }

    private fun setTextWithActiveSimCardNumber() {
        threadScope.launch {
            val activeSimCard = async { viewmodel.getActiveSimCard(this@SmsDetailActivity) }
            activeSimCard.await()
             binding.simCardText.setText(activeSimCard.toString())
        }
    }

    private fun setActiveSimCard(simNumber: Int) {
        threadScope.launch {
            val insert = async {  viewmodel.insertActiveSimCard(SimCard(0, simNumber), this@SmsDetailActivity) }
            insert.await()
            mainScope.launch {
                binding.simCardText.setText(simNumber)
            }
        }
    }

    private fun loadDraftMessageToEditText() {
        // To get the draft message
        lifecycleScope.launch {
            val draftMessage: SmsDetail? = viewmodel.getDraftMessage(this@SmsDetailActivity)
            draftMessage?.let {
                val draftBody: String = draftMessage.body.toString()
                binding.etMessage.setText(draftBody)
            }
        }
    }

    private fun populateMessageRecyclerView() {
        lifecycleScope.launch {

            val messageListForPhoneNumber = async { viewmodel.getMessagesByPhoneNumber(phoneNumber, this@SmsDetailActivity) }.await()
            val joinedlist: MutableList<SmsDetail> = messageListForPhoneNumber

            mainScope.launch {
                adapter.setData(joinedlist)
                val lastItemIndex = joinedlist.size - 1
                recyclerView.scrollToPosition(lastItemIndex)
            }
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun onClickListeners(formattedTimestamp: String) {


        binding.etMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (!isUpdating) {
                    isUpdating = true
                    val newText = s.toString()
                    if (newText.isEmpty()) {
                        threadScope.launch {
                            viewmodel.deleteMessageByTimestamp(phoneNumberStamp, this@SmsDetailActivity)
                        }
                    } else {
                        threadScope.launch {
                            viewmodel.insertSmsDetail(SmsDetail(newText, phoneNumber, phoneNumberStamp, "Draft", 3, formattedTimestamp, "Unsent", name), this@SmsDetailActivity)
                        }
                    }
                    isUpdating = false
                }
            }
        })



        binding.submitSMS.setOnClickListener {

            val message = binding.etMessage.text.toString().trim()

            if (binding.etMessage.text!!.isEmpty()) {
                binding.etMessage.setError("Enter Message")
            } else {

                disableEditTextAndButton()

                threadScope.launch {

                    if (activeSubscriptionInfoList != null && activeSubscriptionInfoList.size >= 2) {
                        sendNormalMessage(message, timestamp, phoneNumber, true)
                    } else {
                        // Less than 2 active SIM cards or no SIM card present
                        sendNormalMessage(message, timestamp, phoneNumber, false)
                    }

                }
            }
        }


        binding.simCard.setOnClickListener {

            if (::bottomSheetBinding.isInitialized) {
                if (activeSubscriptionInfoList != null && activeSubscriptionInfoList.size >= 2) {
                    bottomSheetDialog.show()
                } else {
                    makeLongToast("Only SIM 1 supported")
                }
            }

        }


    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun sendNormalMessage(message: String, timestamp: Long, toMobile: String, dualSim: Boolean?) {

        val formattedTimestamp = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))

        val SENT = "SMS_SENT"
        val DELIVERED = "SMS_DELIVERED"

        sentReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {

                if (resultCode == RESULT_OK) {
                    GlobalScope.launch {
                        val sentTimestamp = System.currentTimeMillis()
                        val myObject = SmsDetail(message, phoneNumber, phoneNumberStamp, "Sent", 2, formattedTimestamp, "Sent - ${sentTimestamp}", name)

                        val _insert = async { viewmodel.insertSmsDetail(myObject, this@SmsDetailActivity) }
                        val insert = _insert.await()

                        Log.d("BROAD-------", "initall: SENT BROAD")
                        try {
                            updateItem(insert)
                            enableButton()
                            context?.unregisterReceiver(sentReceiver)
                            //context?.unregisterReceiver(deliveredReceiver)
                        } catch (e: Exception) {
                            context?.unregisterReceiver(sentReceiver)

                        }
                        viewmodel.deleteMessageByTimestamp(phoneNumberStamp, this@SmsDetailActivity)
                    }
                } else {
                    GlobalScope.launch {
                        val _insert = async { viewmodel.insertSmsDetail(SmsDetail(message, phoneNumber, timestamp, "Draft", 4, formattedTimestamp, "Failed", name), this@SmsDetailActivity) }
                        val insert = _insert.await()
                        try {
                            updateItem(insert)
                            enableButton()
                            context?.unregisterReceiver(sentReceiver)
                        } catch (e: Exception) {
                            Log.d("-------", "initall: ")
                            context?.unregisterReceiver(sentReceiver)
                        }
                    }
                }
            }
        }

        /*deliveredReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (resultCode == RESULT_OK) {
                    val deliveryTimestamp = System.currentTimeMillis()
                    GlobalScope.launch {
                        val _insert = async {
                            viewmodel.insertSmsDetail(
                                SmsDetail(message, phoneNumber, phoneNumberStamp, "Delivered", 1, formattedTimestamp, "Delivered - ${deliveryTimestamp}", name),
                                this@SmsDetailActivity
                            )
                        }
                        val insert = _insert.await()
                        try {
                            updateItem(insert)
                            enableButton()
                        } catch (e: Exception) {
                            Log.d("-------", "initall: ")
                        }
                        viewmodel.deleteMessageByTimestamp(phoneNumberStamp, this@SmsDetailActivity)
                    }
                } else {
                    GlobalScope.launch {
                        val _insert = async { viewmodel.insertSmsDetail(SmsDetail(message, phoneNumber, phoneNumberStamp, "Failed", 4, formattedTimestamp, "Failed", name), this@SmsDetailActivity) }
                        val insert = _insert.await()
                        try {
                            updateItem(insert)
                            enableButton()
                        } catch (e: Exception) {
                            Log.d("-------", "initall: ")
                        }
                    }
                }
            }
        }*/


        val sentPI = PendingIntent.getBroadcast(this, 0, Intent(SENT), PendingIntent.FLAG_IMMUTABLE)
        val deliveredPI = PendingIntent.getBroadcast(this, 0, Intent(DELIVERED), PendingIntent.FLAG_IMMUTABLE)


        if (dualSim == false) {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(toMobile, null, message, sentPI, deliveredPI)
            registerReceiver(sentReceiver, IntentFilter(SENT))
            //registerReceiver(deliveredReceiver, IntentFilter(DELIVERED))
        } else {
            mainScope.launch {
                val theselectedSim = viewmodel.getActiveSimCard(this@SmsDetailActivity)!!.body
                val smsManager = SmsManager.getSmsManagerForSubscriptionId(theselectedSim!!)
                smsManager.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI)
                // Register the broadcast receivers
                registerReceiver(sentReceiver, IntentFilter(SENT))
                //registerReceiver(deliveredReceiver, IntentFilter(DELIVERED))
            }

        }


    }

    private fun updateItem(insert: SmsDetail) {
        mainScope.launch {
            try {
                adapter.updateItem(insert, recyclerView)
            } catch (e: Exception) {
                Log.d("-------", "initall: ")
            }
        }
    }

    private fun disableEditTextAndButton() {
        mainScope.launch {
            binding.etMessage.setText("")
            binding.submitSMS.isVisible = false
            binding.submitLayout.isVisible = false
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


    private fun unregisterBroadcastReceivers() {
        try {
            if (::sentReceiver.isInitialized) {
                Log.d("unregistered-------", "initall: unregistered")
                unregisterReceiver(sentReceiver)
            }
            /* if (::deliveredReceiver.isInitialized) {
                 unregisterReceiver(deliveredReceiver)
             }*/
        } catch (e: IllegalArgumentException) {
            // Handle the exception if the receivers are not registered.
        }
    }


    private fun enableButton() {
        mainScope.launch {
            binding.submitSMS.isVisible = true
            binding.submitLayout.isVisible = true
        }
    }


    override fun onStop() {
        super.onStop()
        unregisterBroadcastReceivers()
    }


}