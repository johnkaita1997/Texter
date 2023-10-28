package com.tafatalkstudent.Activities

import ContactsAdapter
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
import android.provider.Telephony
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tafatalkstudent.Shared.Constants.mainScope
import com.tafatalkstudent.Shared.Constants.pagingConfig
import com.tafatalkstudent.Shared.Contact
import com.tafatalkstudent.Shared.CustomLoadDialogClass
import com.tafatalkstudent.Shared.GroupSmsDetail
import com.tafatalkstudent.Shared.Groups
import com.tafatalkstudent.Shared.MyViewModel
import com.tafatalkstudent.Shared.PostSmsBody
import com.tafatalkstudent.Shared.SmsDetail
import com.tafatalkstudent.Shared.SmsPagingSource
import com.tafatalkstudent.Shared.getContactName
import com.tafatalkstudent.Shared.makeLongToast
import com.tafatalkstudent.databinding.ActivitySmsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class SmsActivity : AppCompatActivity(), LifecycleOwner {


    private lateinit var isthreadScope: CoroutineScope
    private lateinit var smsPagingSource: SmsPagingSource
    private lateinit var pagingData: Flow<PagingData<SmsDetail>>
    private var isFirstTime: Boolean = true
    private lateinit var runnable: Runnable
    private lateinit var binding: ActivitySmsBinding
    private val viewmodel: MyViewModel by viewModels()
    private lateinit var adapter: ContactsAdapter
    private lateinit var cdd: CustomLoadDialogClass
    val handler = Handler()
    val delayMillis: Long = 2000 // 1 second
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
        Log.d("ActivityName", "Current Activity: " + javaClass.simpleName)
        initall()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    private fun initall() {

        isthreadScope = CoroutineScope(Dispatchers.IO)

        smsPagingSource = SmsPagingSource(viewmodel, this)
        pagingData = Pager(config = pagingConfig, pagingSourceFactory = { smsPagingSource }).flow.cachedIn(lifecycleScope)
        pagingData.asLiveData().observe(this@SmsActivity) { pagedData ->
            lifecycleScope.launch {
                adapter.submitData(pagedData)
            }
        }

        sharedPrefs = getSharedPreferences(PREFS_NAME, 0)
        isFirstTime = sharedPrefs.getBoolean(PREF_FIRST_TIME, true)

        cdd = CustomLoadDialogClass(this@SmsActivity)
        cdd.setCanceledOnTouchOutside(false)
        cdd.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        adapter = ContactsAdapter(this)
        val recyclerView: RecyclerView = binding.recyclerviewContacts
        recyclerView.layoutManager = LinearLayoutManager(this@SmsActivity)
        recyclerView.setItemViewCacheSize(500)
        recyclerView.adapter = adapter

        isthreadScope.launch {
            saveMessages(this@SmsActivity)
        }

        isthreadScope.launch {
            saveSmsMessagesToCloud()
        }

    }

    fun saveSmsMessagesToCloud() {

        //GET TOTAL COUNT OF LOCAL MESSAGES
        isthreadScope.launch {

            /*val groupList = viewmodel.getAllGroups(this@SmsActivity)
            groupList.forEach {
                val groupid = it.id

                val groupMessages = viewmodel.getGroupSmsDetailById(groupid!!, this@SmsActivity)

                val totalLocalMessages = viewmodel.getGroupSmsDetailById(it.id, this@SmsActivity).size
                val _totalCloudGroupSms = async { viewmodel.getCloudSmsCount("group", groupid, this@SmsActivity) }
                val totalCloudGroupSms = _totalCloudGroupSms.await()

                if (totalLocalMessages > totalCloudGroupSms) {
                    val difference = totalLocalMessages - totalCloudGroupSms
                    Log.d("GROUPCHECK-------", "initall: GROUP -> ${it.name} HAS ${groupMessages.size} Messages and Cloud has ${totalCloudGroupSms} and difference is ${difference}")
                    //pushTheLastXYGroupMessagesToCloud(difference, it, groupMessages)
                }

            }*/

            val _totalCloudSms = async { viewmodel.getCloudSmsCount("sms", null, this@SmsActivity) }
            val totalCloudSms = _totalCloudSms.await()
            val localMessages = viewmodel.getAllSmsDetails(this@SmsActivity)
            val messagesize = localMessages.size
            val difference = messagesize - totalCloudSms
            if (messagesize > totalCloudSms) {
                Log.d("SMSCHECK-------", "initall: Local -> HAS ${localMessages.size} Messages and Cloud has ${totalCloudSms} and difference is ${difference}")
                pushTheLastXYMessagesToCloud(difference, localMessages)
            } else {
                Log.d("SMSCHECK-------", "initall: Local -> HAS ${localMessages.size} Messages and Cloud has ${totalCloudSms} and difference is ${difference}")
            }

        }


    }

    private fun pushTheLastXYMessagesToCloud(difference: Int, localMessages: MutableList<SmsDetail>) {

        isthreadScope.launch {

            if (localMessages.isNotEmpty()) {
                if (difference > 0) {

                    val deferredList = mutableListOf<Deferred<PostSmsBody>>()

                    val latest12Messages = localMessages.sortedByDescending { it.timestamp }.take(difference)
                    latest12Messages.forEachIndexed { index, smsDetail ->
                        val deferred = async {
                            PostSmsBody(
                                smsDetail.body.toString(),
                                smsDetail.name.toString(), smsDetail.phoneNumber.toString(), smsDetail.state.toString(), smsDetail.status.toString(),
                                smsDetail.formattedTimestamp.toString(), smsDetail.timestamp.toString(), smsDetail.type.toString()
                            )
                        }
                        deferredList.add(deferred)
                    }

                    val batchList = deferredList.awaitAll()

                    // Usage example
                    val batchSize = 500
                    val batches = mutableListOf<List<PostSmsBody>>()
                    for (i in 0 until batchList.size step batchSize) {
                        val batch = batchList.subList(i, minOf(i + batchSize, batchList.size))
                        batches.add(batch)
                    }

                    val _waiter = async {
                        /*batches.forEach {
                            //viewmodel.pushMessages(it.toMutableList(), this@SmsActivity)
                        }*/
                        batches.forEachIndexed { index, postSmsBodies ->
                            Log.d("BATCH-------", "initall: BATCH $index")
                        }
                    }
                    _waiter.await()
                }
            }
        }

    }


    private fun pushTheLastXYGroupMessagesToCloud(difference: Int, group: Groups, groupMessages: MutableList<GroupSmsDetail>) {

        isthreadScope.launch {

            if (groupMessages.isNotEmpty()) {
                if (difference > 0) {

                    val deferredList = mutableListOf<Deferred<GroupSmsDetail>>()
                    val latest12Messages = groupMessages.sortedByDescending { it.timestamp }.take(difference)
                    latest12Messages.forEachIndexed { index, smsDetail ->
                        val deferred = async { smsDetail }
                        deferredList.add(deferred)
                    }

                    val batchList = deferredList.awaitAll()

                    // Usage example
                    val batchSize = 500
                    val batches = mutableListOf<List<GroupSmsDetail>>()
                    for (i in 0 until batchList.size step batchSize) {
                        val batch = batchList.subList(i, minOf(i + batchSize, batchList.size))
                        batches.add(batch)
                    }

                    val _waiter = async {
                        batches.forEach {
                            viewmodel.pushGroupMessage(it.toMutableList(), this@SmsActivity)
                        }
                    }
                    _waiter.await()

                }
            }
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
                val name = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val phoneNumber = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
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
        isthreadScope.launch {

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
                    isthreadScope.launch {
                        saveSmsMessagesToCloud()
                    }
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

        isthreadScope.launch {

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
                    refreshData()
                    Log.d("CALLED-------", "initall: FINAL")
                }
            }

        }


    }


    // Call this function when you want to refresh your data
    private fun refreshData() {
        smsPagingSource.invalidate() // Invalidate the paging source
    }


    private fun checkForNewMessages() {
        runnable = object : Runnable {
            override fun run() {
                isthreadScope.launch {

                    val cursor = this@SmsActivity.contentResolver.query(uri, null, null, null, null)
                    val numberofItems = cursor!!.count
                    cursor.close()

                    val totalSmsDetailCount = async { viewmodel.getTotalSmsDetailCount(this@SmsActivity) }
                    val numberOfDraftItems = async { viewmodel.getDraftSmsCount(this@SmsActivity) }

                    val standingSmsCount = totalSmsDetailCount.await() - numberOfDraftItems.await()

                    val difference = numberofItems - standingSmsCount
                    val message = "Curosor Count -> ${numberofItems}    Standing Db Count ->${standingSmsCount}    Difference -> ${difference}\n"
                    Log.d("tracker-------", "initall: $message")
                    if (difference > 0) {

                        Log.d("CALLED-------0", "initall: CURSOR SIZE = $numberofItems AND ")

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

                                        isthreadScope.launch {
                                            var isread = false
                                            isread = state == "Sent"
                                            val smsDetail = SmsDetail(body, phoneNumber, timestamp, state, status, formattedTimestamp, deliveryStatus, name, isread)
                                            batchList.add(smsDetail)
                                            Log.d("herere-------", "initall: Here........................................")
                                        }

                                    } while (it.moveToNext())
                                } else {
                                    latestCursor?.close()
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


                        isthreadScope.launch {
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
                            isthreadScope.launch {
                                saveSmsMessagesToCloud()
                            }
                        } catch (e: Exception) {
                            Log.d("-------", "initall: ")
                        }

                    } else {
                        Log.d("CALLED-------0", "initall: NOT BIGGER THAN")
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
        isthreadScope.launch {
            val delete = async { viewmodel.deleteMessagesWithPattern(this@SmsActivity) }
            delete.await()
            updateSmsList()
        }
    }


    override fun onPause() {
        super.onPause()
        if (::runnable.isInitialized) handler.removeCallbacks(runnable)
        isthreadScope.cancel()
        lifecycleScope.cancel()
    }

    override fun onStop() {
        super.onStop()
        if (::runnable.isInitialized) handler.removeCallbacks(runnable)
        isthreadScope.cancel()
        lifecycleScope.cancel()
    }

    override fun onResume() {
        super.onResume()
        deleExistingMessagesAndUpdate()
        if (!isFirstTime) {
            if (!isthreadScope.isActive) {
                isthreadScope = CoroutineScope(Dispatchers.IO)
            }
            checkForNewMessages()
            isthreadScope.launch {
                saveSmsMessagesToCloud()
            }
        }
    }


}