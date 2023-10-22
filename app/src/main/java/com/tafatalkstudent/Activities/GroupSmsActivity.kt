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
import android.telephony.TelephonyManager
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
import com.tafatalkstudent.Shared.Contact
import com.tafatalkstudent.Shared.GroupSmsDetail
import com.tafatalkstudent.Shared.MyViewModel
import com.tafatalkstudent.Shared.SimCard
import com.tafatalkstudent.Shared.formatPhoneNumber
import com.tafatalkstudent.Shared.getContactName
import com.tafatalkstudent.Shared.getPhoneNumberForSubscriptionId
import com.tafatalkstudent.Shared.makeLongToast
import com.tafatalkstudent.Shared.showAlertDialog
import com.tafatalkstudent.databinding.ActivityGroupSmsBinding
import com.tafatalkstudent.databinding.BottomsheetBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class GroupSmsActivity : AppCompatActivity() {

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var activeSubscriptionInfoList: MutableList<SubscriptionInfo>
    private lateinit var subscriptionManager: SubscriptionManager

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    @SuppressLint("MissingPermission")

    private lateinit var bottomSheetBinding: BottomsheetBinding
    private var phoneNumberStamp = 9999999999999999

    //private lateinit var deliveredReceiver: BroadcastReceiver
    private lateinit var binding: ActivityGroupSmsBinding
    private val viewmodel: MyViewModel by viewModels()
    private lateinit var adapter: GroupSmsDetailAdapter
    private lateinit var recyclerView: RecyclerView
    private var isUpdating = false // Add this variable to track ongoing updates

    private var groupId: Long = 0
    private lateinit var groupName: String
    private lateinit var groupDescription: String
    private lateinit var senderNumber: String
    private lateinit var senderName: String
    private lateinit var theselectedSimCard: SimCard
    private lateinit var groupMembers: MutableList<Contact>

    companion object {
        var currentTimestamp: Long = 0
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupSmsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("ActivityName", "Current Activity: " + javaClass.simpleName)
        initall()
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun initall() {

        subscriptionManager = getSystemService(SubscriptionManager::class.java)
        activeSubscriptionInfoList = subscriptionManager.activeSubscriptionInfoList

        setUpSendNameAndSenderPhoneNumber()

        groupName = intent.getStringExtra("groupName").toString()
        groupId = intent.getStringExtra("groupId").toString().toLong()
        groupDescription = intent.getStringExtra("groupDescription").toString()

        threadScope.launch {
            val _group = async { viewmodel.getGroupById(groupId, this@GroupSmsActivity) }
            val members = _group.await()?.members
            members?.let {
                groupMembers = it
            }
        }

        makeIsReadToTrue(groupId)
        setTextWithActiveSimCardNumber()

        bottomSheetBinding = BottomsheetBinding.inflate(LayoutInflater.from(this))
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetBinding.sim1.setOnClickListener {
            setActiveSimCard(1)
        }
        bottomSheetBinding.sim2.setOnClickListener {
            setActiveSimCard(2)
        }



        recyclerView = binding.recyclerViewMessageDetails

        binding.textViewNameOfPerson.setText(groupName)

        adapter = GroupSmsDetailAdapter(viewmodel, this@GroupSmsActivity, binding.etMessage)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setItemViewCacheSize(100)
        recyclerView.adapter = adapter

        populateMessageRecyclerView()

        loadDraftMessageToEditText()

        onClickListeners()

    }

    private fun makeIsReadToTrue(groupId: Long) {
        threadScope.launch {
            viewmodel.markAllGroupMessagesAsRead(groupId, this@GroupSmsActivity)
        }
    }


    private fun setTextWithActiveSimCardNumber() {
        threadScope.launch {
            val _activeSimCard = async { viewmodel.getActiveSimCard(this@GroupSmsActivity) }
            val activeSimCard = _activeSimCard.await()
            binding.simCardText.setText(activeSimCard!!.body.toString())
        }
    }

    private fun setActiveSimCard(simNumber: Int) {
        threadScope.launch {
            val insert = async { viewmodel.insertActiveSimCard(SimCard(0, simNumber), this@GroupSmsActivity) }
            insert.await()
            mainScope.launch {
                binding.simCardText.setText(simNumber)
            }
        }
    }

    private fun loadDraftMessageToEditText() {
        // To get the draft message
        lifecycleScope.launch {
            val draftMessage: GroupSmsDetail? = viewmodel.getLatestGroupDraftMessage(this@GroupSmsActivity)
            draftMessage?.let {
                val draftBody: String = draftMessage.body.toString()
                binding.etMessage.setText(draftBody)
            }
        }
    }

    private fun populateMessageRecyclerView() {
        lifecycleScope.launch {
            val messagelist = async { viewmodel.getGroupSmsDetailByIdUniqueCodeStamp(groupId, this@GroupSmsActivity) }.await()
            mainScope.launch {
                adapter.setData(messagelist)
                val lastItemIndex = messagelist.size - 1
                recyclerView.scrollToPosition(lastItemIndex)
            }
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun onClickListeners() {

        binding.etMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val newText = s.toString()
                if (newText.isEmpty()) {
                    threadScope.launch {
                        viewmodel.deleteGroupMessageByTimestamp(999999999, this@GroupSmsActivity)
                    }
                } else {
                    threadScope.launch {
                        val codestamp = System.nanoTime()
                        val formattedTimestamp = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(codestamp))
                        val myObject = GroupSmsDetail(999999999,
                            newText, "999999999", 999999999, "Draft", 1, formattedTimestamp,
                            "Sent", true, groupId, groupName, senderName, senderNumber, codestamp
                        )
                        val _insert = async { viewmodel.insertGroupSmsDetail(myObject, this@GroupSmsActivity) }
                        _insert.await()
                    }
                }
                isUpdating = false
            }
        })



        binding.submitSMS.setOnClickListener {

            val message = binding.etMessage.text.toString().trim()

            if (binding.etMessage.text!!.isEmpty()) {
                binding.etMessage.setError("Enter Message")
            } else {

                disableEditTextAndButton()

                threadScope.launch {

                    val codestamp = System.nanoTime()
                    val formattedTimestamp = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(codestamp))

                    if (activeSubscriptionInfoList != null && activeSubscriptionInfoList.size >= 2) {
                        sendNormalMessage(message, codestamp ,true)
                    } else {
                        // Less than 2 active SIM cards or no SIM card present
                        sendNormalMessage(message, codestamp, false)
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


    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun sendNormalMessage(message: String, codeStamp: Long, dualSim: Boolean?) {

        groupMembers.forEachIndexed { index, contact ->
            val phoneNumber = contact.phoneNumber

            currentTimestamp = System.nanoTime()
            val formattedTimestamp = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(currentTimestamp))

            val SENT = "SMS_SENT"
            val DELIVERED = "SMS_DELIVERED"

            val sentReceiver: BroadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {

                    if (resultCode == RESULT_OK) {
                        GlobalScope.launch {
                            val myObject = GroupSmsDetail(null,
                                message, phoneNumber, currentTimestamp, "Sent", 1, formattedTimestamp,
                                "Sent", true, groupId, groupName, senderName, senderNumber, codeStamp
                            )
                            val _insert = async { viewmodel.insertGroupSmsDetail(myObject, this@GroupSmsActivity) }
                            _insert.await()
                            viewmodel.deleteGroupMessageByTimestamp(999999999, this@GroupSmsActivity)
                            viewmodel.deleteGroupMessageByTimestampAndDraft(currentTimestamp, this@GroupSmsActivity)
                        }
                        context?.unregisterReceiver(this)
                    } else {
                        GlobalScope.launch {
                            viewmodel.deleteGroupMessageByTimestamp(999999999, this@GroupSmsActivity)
                            val myObject = GroupSmsDetail(null,
                                message, phoneNumber, currentTimestamp, "Failed", 1, formattedTimestamp,
                                "Failed", true, groupId, groupName, senderName, senderNumber, codeStamp
                            )
                            val _insert = async { viewmodel.insertGroupSmsDetail(myObject, this@GroupSmsActivity) }
                            _insert.await()
                            viewmodel.deleteGroupMessageByTimestampAndDraft(currentTimestamp, this@GroupSmsActivity)
                        }
                        context?.unregisterReceiver(this)
                    }
                }
            }

            val sentPI = PendingIntent.getBroadcast(this, 0, Intent(SENT), PendingIntent.FLAG_IMMUTABLE)
            val deliveredPI = PendingIntent.getBroadcast(this, 0, Intent(DELIVERED), PendingIntent.FLAG_IMMUTABLE)


            if (dualSim == false) {

                if (formatPhoneNumber(senderNumber) != formatPhoneNumber(phoneNumber.toString())) {

                    Log.d("Dual-------", "initall: It is not Dual")

                    val smsManager = SmsManager.getDefault()
                    smsManager.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI)
                    val myObject = GroupSmsDetail(null,
                        message, phoneNumber, 999999999, "Pending", 1, formattedTimestamp,
                        "Not Sent", true, groupId, groupName, senderName, senderNumber,codeStamp
                    )
                    threadScope.launch {
                        viewmodel.insertGroupSmsDetail(myObject, this@GroupSmsActivity)
                    }

                    mainScope.launch {
                        if (index == groupMembers.size-1) {
                            updateItem(myObject)
                        }
                        enableButton()
                    }

                    registerReceiver(sentReceiver, IntentFilter(SENT))

                }

            } else {

                mainScope.launch {

                    if (!::theselectedSimCard.isInitialized) {
                        theselectedSimCard = viewmodel.getActiveSimCard(this@GroupSmsActivity)!!
                    }

                    val theselectedSim = theselectedSimCard.body


                    if (formatPhoneNumber(senderNumber) != formatPhoneNumber(phoneNumber.toString())) {

                        Log.d("Dual-------", "initall: It is Dual")

                        val smsManager = theselectedSim?.let { it1 -> SmsManager.getSmsManagerForSubscriptionId(it1) }
                        smsManager?.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI)

                        val myObject = GroupSmsDetail(null,
                            message, phoneNumber, 999999999, "Pending", 1, formattedTimestamp,
                            "Not Sent", true, groupId, groupName, senderName, senderNumber,codeStamp
                        )

                        threadScope.launch {
                            viewmodel.insertGroupSmsDetail(myObject, this@GroupSmsActivity)
                        }

                        mainScope.launch {
                            if (index == groupMembers.size-1) {
                                updateItem(myObject)
                            }
                            enableButton()
                        }
                        registerReceiver(sentReceiver, IntentFilter(SENT))

                    }

                }

            }
            threadScope.launch { delay(1000) }
        }

    }

    private fun updateItem(insert: GroupSmsDetail) {
        mainScope.launch {
            try {
                adapter.updateItem(insert, recyclerView)
            } catch (e: Exception) {
            }
        }
    }

    private fun disableEditTextAndButton() {
        mainScope.launch {
            binding.etMessage.setText("")
            binding.submitSMS.isVisible = false
            binding.submitLayout.isVisible = false
            binding.spinkitLayout.visibility = View.VISIBLE
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
            binding.submitSMS.isVisible = true
            binding.submitLayout.isVisible = true
            binding.spinkitLayout.visibility = View.GONE
        }
    }


    override fun onStop() {
        super.onStop()
    }

    @SuppressLint("NewApi", "MissingPermission")
    fun setUpSendNameAndSenderPhoneNumber() {

        if (activeSubscriptionInfoList != null && activeSubscriptionInfoList.size >= 2) {
            threadScope.launch {
                if (!::theselectedSimCard.isInitialized) {
                    theselectedSimCard = viewmodel.getActiveSimCard(this@GroupSmsActivity)!!
                }
                val theselectedSim = theselectedSimCard.body
                senderNumber = getPhoneNumberForSubscriptionId(theselectedSim!!)
                if (!::senderName.isInitialized) {
                    senderName = getContactName(senderNumber)
                }
            }
        } else {
            // Less than 2 active SIM cards or no SIM card present
            val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            senderNumber = telephonyManager.line1Number ?: "Not Available"
            if (!::senderName.isInitialized) {
                senderName = getContactName(senderNumber)
            }
        }


    }


}