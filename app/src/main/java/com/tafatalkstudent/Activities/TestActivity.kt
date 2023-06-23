package com.tafatalkstudent.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.provider.CallLog
import android.provider.ContactsContract
import android.provider.Settings
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tafatalkstudent.R
import com.tafatalkstudent.Shared.*
import com.tafatalkstudent.databinding.ActivityTestBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.text.DecimalFormat


@AndroidEntryPoint
class TestActivity : AppCompatActivity(), LifecycleOwner {

    private lateinit var binding: ActivityTestBinding
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
        var activeMobile: String? = ""
        var minimum_Device_Token_Balance_To_Allow_Calls: Double = 0.0
        var minimum_Overall_School_Minute_Balance_To_Allow_Calls: Double = 0.0
        var minimum_Student_Token_Balance_To_Make_Calls: Double = 0.0
        var deviceTokenBalance: Double = 0.0
        var deviceMinuteBalance: Double = 0.0
        var mobileactive: Boolean = false
        var isStudentActive: Boolean = false
        var theschoolNumber: String = ""
        private const val REQUEST_DEFAULT_PHONE_HANDLER = 100
        private const val PERMISSION_REQUEST_PHONE_STATE = 101
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activeMobile = getMobileNumberFromSimContact("admin").toString()
        checkPermissionsAndSetDefaultHandler()
        setGlobalSettings()
        initall()
    }

    private fun setGlobalSettings() {
        CoroutineScope(Dispatchers.IO).launch() {
            val _settings = async { viewmodel.getGlobalSettings(this@TestActivity) }
            val settings = _settings.await()
            minimum_Device_Token_Balance_To_Allow_Calls = settings.minimum_Device_Token_Balance_To_Allow_Calls
            minimum_Overall_School_Minute_Balance_To_Allow_Calls = settings.minimum_Overall_School_Minute_Balance_To_Allow_Calls
            minimum_Student_Token_Balance_To_Make_Calls = settings.minimum_Student_Token_Balance_To_Make_Calls
            val _deviceToken = async { viewmodel.getDeviceBalance(activeMobile.toString(), this@TestActivity) }
            deviceTokenBalance = _deviceToken.await().standingtoken!!
            deviceMinuteBalance = _deviceToken.await().standingminutes!!
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("Range")
    private fun initall() {
        showProgress(this)
        binding.logutbutton.setOnClickListener {
            logoutUser(studentid.toString())
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
                    val constant = async { viewmodel.getConstantResults(schoolid!!, this@TestActivity) }
                    constant.await()

                    val studentList = async { viewmodel.getstudentlist(myuserid.toString(), this@TestActivity) }
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
            val schoolid = it.get(0).school.id
            val contactlist = it.get(0).contacts
            isStudentActive = it.get(0).active
            studentresult = it

            val welcomeText = "Welcome "
            val studentName = studentidname
            val spannableString = SpannableString(welcomeText + studentName)
            spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.defaultColor)), 0, welcomeText.length, 0)
            spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.white)), welcomeText.length, spannableString.length, 0)
            binding.welcomeUser.text = spannableString

            binding.tokenbalance.setText(df.format(tokenbalance).toString())

            CoroutineScope(Dispatchers.IO).launch(coroutineexception(this)) {

                val schoolDetails = async { viewmodel.getSchoolDetails(schoolid.toString(), this@TestActivity) }
                val balance = async { viewmodel.getStandingTokenForSchool(schoolid.toString(), this@TestActivity) }
                val mobileid = async { viewmodel.getMobileId(activeMobile.toString(), this@TestActivity) }
                val _mobileactive = async { viewmodel.getMobile(mobileid.await(), this@TestActivity) }

                var equivalentminutes = 0.0

                withContext(Dispatchers.Main) {
                    binding.welcomeschool.setText(schoolDetails.await().name)
                    if (constant != null) {
                        val minutespertokenOrequivalentminutes = constant!!.get(0).minutespertokenOrequivalentminutes
                        val minutepershilling = constant!!.get(0).minutepershilling
                        val shillingspertokenOrequivalentshillings = constant!!.get(0).shillingspertokenOrequivalentshillings
                        val activationamount = constant!!.get(0).activationamount
                        equivalentminutes = ((tokenbalance!! * minutespertokenOrequivalentminutes).toDouble())
                        binding.relativeminutes.setText("${df.format(equivalentminutes)} Mins")
                    }

                    mobileactive = _mobileactive.await().active!!
                    if (mobileactive == false) {
                        dismissProgress()
                        binding.layoutcontrol.visibility = View.INVISIBLE
                        showAlertDialog("This device is inactive!")
                    } else if (isStudentActive == false) {
                        dismissProgress()
                        binding.layoutcontrol.visibility = View.INVISIBLE
                        binding.activationtext.visibility = View.VISIBLE
                    } else {
                        val schoolMinutesBalance = balance.await()
                        if (schoolMinutesBalance <= minimum_Overall_School_Minute_Balance_To_Allow_Calls) {
                            dismissProgress()
                            showAlertDialog("School Mobile Minutes Have Been Exhausted!")
                            binding.callcontrollayout.visibility = View.INVISIBLE
                        } else {

                            if (deviceTokenBalance <= minimum_Device_Token_Balance_To_Allow_Calls) {
                                dismissProgress()
                                showAlertDialog("Device Tokens Have Been Exhausted!")
                                binding.callcontrollayout.visibility = View.INVISIBLE
                            } else {
                                if (tokenbalance!! <= minimum_Student_Token_Balance_To_Make_Calls) {
                                    dismissProgress()
                                    showAlertDialog("You have insufficient tokens left to make calls")
                                    binding.callcontrollayout.visibility = View.INVISIBLE
                                } else {

                                    binding.callcontrollayout.visibility = View.VISIBLE
                                    binding.activationtext.visibility = View.GONE
                                    binding.layoutcontrol.visibility = View.VISIBLE

                                    val layoutManager = LinearLayoutManager(this@TestActivity)
                                    val contactsadapter = ContactsAdapter(this@TestActivity, mutableListOf(), viewmodel, phoneStateReceiver, tokenbalance, equivalentminutes)
                                    binding.contactsyoucancallrecyclerview.setLayoutManager(layoutManager)
                                    binding.contactsyoucancallrecyclerview.adapter = contactsadapter
                                    contactsadapter.updateContactsAdapter(contactlist)

                                }
                            }
                        }

                        GlobalScope.launch { fetchCreateCallLog() }
                    }

                }

            }

        })

        CoroutineScope(Dispatchers.IO).launch() {
            val fineUserDetails = async { viewmodel.getFineUserDetails(this@TestActivity) }
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
                        sendCallInfo(mobilecalled, callType, duration, ringingTime.toString(), callstamp.toString(), activeMobile.toString(), viewmodel, iteration, mobilecalled)

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
        mobileCalled: String, callType: Int, duration: String, ringingTime: String, callstamp: String, schoolNumber: String, viewmodel: MyViewModel, iteration: Int, contactName: String
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
                        "------",
                        "SAVING  old token is ${oldstudenttokenbalance} " + " and new token is ${newstudenttokenbalance} " + "  and minutesconsumed is ${callminutesconsumed}  and tokens used is ${tokensused}"
                    )
                }

                /*val _username = async {viewmodel.getUserWithNumber(mobileCalled) }
                val username = _username.await().getOrNull(0)?.fullname ?: mobileCalled*/
                val callLogbody = CreateCallLog(callstamp, duration, callminutesconsumed, mobileCalled, studentid!!.toInt(), tokensused, schoolNumber, mobileCalled)

                val mobileid = async { viewmodel.getMobileId(activeMobile.toString(), this@TestActivity) }

                val database = RoomDb(this@TestActivity).loginDao()
                val listOfLoginsMatchingDateOfCall = database.findLoginForTimestamp(callstamp.toLong())
                if (listOfLoginsMatchingDateOfCall.isEmpty()) {
                    Log.d("-------", "initall: No user logged found for call Time: ${callstamp} - Duration: $duration -Mobile Called: $mobileCalled - Mins: $callminutesconsumed")
                } else {
                    val thestudentId = listOfLoginsMatchingDateOfCall.get(0).studentId.toInt()
                    this@TestActivity.viewmodel.createCallLog(
                        schoolid!!, callLogbody, thestudentId, newstudenttokenbalance, userid, mobileid.await().toInt(), callminutesconsumed, tokensused, this@TestActivity
                    )
                }

            }
        }

    }


    @SuppressLint("Range")
    fun getMobileNumberFromSimContact(contactName: String): String {
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val selection = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(contactName)
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
        var phoneNumber: String? = null
        cursor?.use {
            if (it.moveToFirst()) {
                phoneNumber = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            }
        }
        cursor?.close()
        return phoneNumber ?: "25490"
    }


    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            if (isStudentActive && mobileactive) {
                fetchCreateCallLog()
            }
        }
    }


    override fun onBackPressed() {
        val alert = android.app.AlertDialog.Builder(this).setTitle("Tafa Talk").setCancelable(false).setMessage("Are you sure you want to exit").setIcon(R.drawable.logodark)
            .setPositiveButton("Exit", { dialog, _ ->
                dialog.dismiss()
                finish()
            }).setNegativeButton("Dismis", { dialog, _ -> dialog.dismiss() }).show()
    }

    private fun checkPermissionsAndSetDefaultHandler() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), PERMISSION_REQUEST_PHONE_STATE)
        } else {
            setDefaultPhoneHandler()
        }
    }


    private fun setDefaultPhoneHandler() {
        val telecomManager = getSystemService(TELECOM_SERVICE) as TelecomManager
        if (packageName != telecomManager.defaultDialerPackage) {
            val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
                .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
            startActivityForResult(intent, REQUEST_DEFAULT_PHONE_HANDLER)
        } else {
            //  handleIncomingCall()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_PHONE_STATE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setDefaultPhoneHandler()
            } else {
                finish()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_DEFAULT_PHONE_HANDLER) {
            if (isDefaultPhoneHandler()) {
//                handleIncomingCall()
            } else {
                showDefaultPhoneHandlerPrompt()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isDefaultPhoneHandler(): Boolean {
        val telecomManager = getSystemService(TELECOM_SERVICE) as TelecomManager
        return packageName == telecomManager.defaultDialerPackage
    }


    private fun showDefaultPhoneHandlerPrompt() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Set as Default Phone Handler")
            .setMessage("In order for this app to work correctly, please set this app as your default phone handler.")
            .setPositiveButton("Go to Settings") { dialog, which ->
                openDefaultPhoneHandlerSettings()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
                finish()
            }
            .setCancelable(false)
        builder.create()
        builder.show()
    }


    private fun openDefaultPhoneHandlerSettings() {
        val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
        startActivity(intent)
    }


}



