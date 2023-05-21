package com.propswift.Activities

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CancellationSignal
import android.provider.CallLog
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.propswift.Shared.*
import com.propswift.databinding.ActivityTestBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.text.DecimalFormat


@AndroidEntryPoint
class TestActivity : AppCompatActivity(), LifecycleOwner {

    private lateinit var binding: ActivityTestBinding
    private lateinit var callStateListener: PhoneStateListener
    private lateinit var telephonyManager: TelephonyManager
    var studentid: Int? = null
    var mobileid: Int? = null
    var tokenbalance: Float? = null
    lateinit var userid: String
    var studentresult: GetStudentResult? = null
    var constant: GetConstantsResult? = null
    private val viewmodel: MyViewModel by viewModels()
    val df = DecimalFormat("#.##")

    companion object {
        var school: School? = null
        var schoolid: String? = null
        var myuserid: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initall()
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("Range")
    private fun initall() {

        showProgress(this)

        binding.logutbutton.setOnClickListener {
            logoutUser()
        }

        val phoneStateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
                    val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                    if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                        lifecycleScope.launch {
                            Log.d("-------", "initall: This call has finally ended")
                            fetchCreateCallLog()
                        }
                    }
                }
            }
        }


        viewmodel.userfinedetails.observe(this, {
            myuserid = it?.get(0)?.id
            school = it?.get(0)?.school
            schoolid = it?.get(0)?.school?.id.toString()
            userid = myuserid.toString()

            lifecycleScope.launch {
                if (schoolid != null) {
                    val constant = async { viewmodel.getConstantResults(schoolid!!) }
                    constant.await()

                    val studentList = async { viewmodel.getstudentlist(myuserid.toString()) }
                    studentList.await()

                } else {
                    withContext(Dispatchers.Main) {
                        showAlertDialog("School ID is Null")
                    }
                }
            }

        })

        viewmodel.constants.observe(this, {
            constant = it
            Log.d("-------sawaaaaaa", "initall: ${it}")
            val minutespertokenOrequivalentminutes = it?.get(0)?.minutespertokenOrequivalentminutes
            val minutepershilling = it?.get(0)?.minutepershilling
            val shillingspertokenOrequivalentshillings = it?.get(0)?.shillingspertokenOrequivalentshillings
            val activationamount = it?.get(0)?.activationamount
        })

        viewmodel.studentloggedin.observe(this, {
            val studentidname = it?.get(0)?.fullname
            val studentidentity = it?.get(0)?.id
            studentid = studentidentity!!
            tokenbalance = it.get(0).tokenbalance

            val school = it.get(0).school.name
            mobileid = it.get(0).school.mobile.id
            val schoolMinutesBalance = it.get(0).school.mobile.standingminutes

            val contactlist = it.get(0).contacts
            val isactive = it.get(0).active
            studentresult = it

            dismissProgress()

            binding.welcomeUser.setText("Welcome ${studentidname}")
            binding.welcomeschool.setText(school.toString())
            binding.tokenbalance.setText(df.format(tokenbalance).toString())
            if (constant != null) {
                val minutespertokenOrequivalentminutes = constant!!.get(0).minutespertokenOrequivalentminutes
                val minutepershilling = constant!!.get(0).minutepershilling
                val shillingspertokenOrequivalentshillings = constant!!.get(0).shillingspertokenOrequivalentshillings
                val activationamount = constant!!.get(0).activationamount
                val equivalentminutes = (tokenbalance!! * minutespertokenOrequivalentminutes)
                binding.relativeminutes.setText("${df.format(equivalentminutes)} Mins")
            }

            if (isactive == false) {
                binding.layoutcontrol.visibility = View.INVISIBLE
                binding.activationtext.visibility = View.VISIBLE
            } else {
                if (schoolMinutesBalance <= 0) {
                    showAlertDialog("School Mobile Minutes Have Been Exhausted!")
                    binding.callcontrollayout.visibility = View.INVISIBLE
                } else {
                    if (tokenbalance!! <= 0) {
                        showAlertDialog("You have insufficient tokens left to make calls")
                        binding.callcontrollayout.visibility = View.INVISIBLE
                    } else {
                        binding.callcontrollayout.visibility = View.VISIBLE
                        binding.activationtext.visibility = View.GONE
                        binding.layoutcontrol.visibility = View.VISIBLE

                        val layoutManager = LinearLayoutManager(this)
                        val contactsadapter = ContactsAdapter(this, mutableListOf(), viewmodel, phoneStateReceiver, tokenbalance)
                        binding.contactsyoucancallrecyclerview.setLayoutManager(layoutManager)
                        binding.contactsyoucancallrecyclerview.adapter = contactsadapter
                        contactsadapter.updateContactsAdapter(contactlist)

                    }
                }
            }

            GlobalScope.launch {
                async { fetchCreateCallLog() }
            }

        })


        CoroutineScope(Dispatchers.IO).launch() {

            val fineUserDetails = async { viewmodel.getFineUserDetails() }
            fineUserDetails.await()

        }


    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("Range")
    private fun fetchCreateCallLog() {

        if (constant == null) {
            //showAlertDialog("Missing Info, try again later")
        } else {
            if (studentid != null) {

                val schoolNumber = fetchSchoolNumber()

                // Get the most recent call
                val missedCallType = CallLog.Calls.MISSED_TYPE
                val cancellationSignal = CancellationSignal()
                val projection = arrayOf(CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION)
                val cursor = contentResolver.query(CallLog.Calls.CONTENT_URI, projection, null, null, CallLog.Calls.DATE + " DESC", cancellationSignal)


                val count = cursor?.count ?: 0
                //Log.d("---------", "CallLogCount Found $count call logs")

                var iteration = 0

                while (cursor != null && cursor.moveToNext() && iteration < 20) {

                    // Get the call details
                    val mobilecalled = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))
                    val callType = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE))
                    val duration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION))
                    val callstamp = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE))

                    // Check if the call was accepted
                    if (callType != missedCallType) {

                        val date = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE)).toLong()
                        val ringingTime = ((date + duration.toLong()) - cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE)).toLong()) / 1000

                        // Send the information
                        sendCallInfo(mobilecalled, callType, duration, ringingTime.toString(), callstamp.toString(), schoolNumber, viewmodel, iteration, mobilecalled)

                    } else {
                        Log.d("----------", "It is a missed call")
                    }

                    iteration = iteration + 1

                }

                // Close the cursor to avoid memory leaks
                cursor?.close()

            } else {
                Log.d("----------", "Student Id is null")
            }
        }

    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun sendCallInfo(
        mobileCalled: String,
        callType: Int,
        duration: String,
        ringingTime: String,
        callstamp: String,
        schoolNumber: String,
        viewmodel: MyViewModel,
        iteration: Int,
        contactName: String
    ) {

        CoroutineScope(Dispatchers.IO).launch() {
            studentid.let {

                val minutespertokenOrequivalentminutes = constant!!.get(0).minutespertokenOrequivalentminutes
                val callminutesconsumed = duration.toFloat() / 60
                val tokensused = callminutesconsumed / minutespertokenOrequivalentminutes

                val oldstudenttokenbalance = studentresult?.get(0)?.tokenbalance
                val newstudenttokenbalance = oldstudenttokenbalance?.minus(tokensused)

                if (iteration == 0) {
                    Log.d(
                        "------", "SAVING  old token is ${oldstudenttokenbalance} " +
                                " and new token is ${newstudenttokenbalance} " +
                                "  and minutesconsumed is ${callminutesconsumed}  and tokens used is ${tokensused}"
                    )
                }

                /*val _username = async {viewmodel.getUserWithNumber(mobileCalled) }
                val username = _username.await().getOrNull(0)?.fullname ?: mobileCalled*/

                val callLogbody = CreateCallLog(callstamp, duration, callminutesconsumed, mobileCalled, studentid!!.toInt(), tokensused, schoolNumber, mobileCalled)
                this@TestActivity.viewmodel.createCallLog(schoolid!!, callLogbody, studentid!!, newstudenttokenbalance, userid, mobileid, callminutesconsumed, tokensused)

            }
        }

    }


    @SuppressLint("MissingPermission")
    private fun fetchSchoolNumber(): String {

        var simOneNumber = "Unavailable"

        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val simNumber = telephonyManager.getLine1Number()

        if (!simNumber.isNullOrEmpty()) {
            // Use simNumber as required
            simOneNumber = simNumber
        } else {
            // SIM 1 number is not available
        }

        return simOneNumber
    }


    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            fetchCreateCallLog()
        }
    }


}


