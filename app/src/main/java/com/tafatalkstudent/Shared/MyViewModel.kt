package com.tafatalkstudent.Shared

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.tafatalkstudent.Activities.LauncherActivity
import com.tafatalkstudent.Activities.TestActivity
import com.tafatalkstudent.Retrofit.MyApi
import com.tafatalkstudent.Shared.Constants.mainScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dmax.dialog.SpotsDialog
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Named
import kotlin.collections.set
import kotlin.coroutines.CoroutineContext


@HiltViewModel
class MyViewModel
@Inject constructor(
    @Named("myapi") private val api: MyApi, @ApplicationContext private val appcontext: Context
) : ViewModel() {

    val _userfinedetails = MutableLiveData<UserFineDetails>()
    val userfinedetails: LiveData<UserFineDetails?> get() = _userfinedetails

    val _studentloggedin = MutableLiveData<GetStudentResult>()
    val studentloggedin: LiveData<GetStudentResult?> get() = _studentloggedin

    val _constants = MutableLiveData<GetConstantsResult>()
    val constants: LiveData<GetConstantsResult?> get() = _constants

    val _isParentAccountActive = MutableLiveData<Boolean?>()
    val isParentAccountActive: LiveData<Boolean?> get() = _isParentAccountActive

    val _contactmodelofloggedinuser = MutableLiveData<GetContactModelOfLoggedInUser>()
    val contactmodelofloggedinuser: LiveData<GetContactModelOfLoggedInUser?> get() = _contactmodelofloggedinuser

    val _bothNames = MutableLiveData<String>()
    val bothNames: LiveData<String> get() = _bothNames

    val _totalAmount = MutableLiveData<String>()
    val totalAmount: LiveData<String> get() = _totalAmount

    val _getTotalNumberofReceipts = MutableLiveData<String>()
    val getTotalNumberofReceipts: LiveData<String> get() = _getTotalNumberofReceipts

    val _listRentals = MutableLiveData<MutableList<RentDetail>>()
    val listRentals: LiveData<MutableList<RentDetail>> get() = _listRentals


    fun Context.coroutineexception(activity: Activity): CoroutineContext {
        val handler = CoroutineExceptionHandler { _, exception ->
            activity.runOnUiThread {
                if (activityisrunning()) {
                    showAlertDialog(exception.toString())
                    activity.dismissProgress()
                }
                return@runOnUiThread
            }
        }
        return handler
    }


    suspend fun networkResponseFailure(it: Throwable, mydialog: SpotsDialog?, modelname: String, activity: Activity) {
        if (activity.activityisrunning()) {
            withContext(Dispatchers.Main) {
                Log.d("----------", "networkResponseFailure at ${modelname}: KERROR - ${it.message} \n ${it.fillInStackTrace()} \n\n ${it.cause} \n\n ${it.localizedMessage} \n\n${it.stackTrace}")
                activity.showAlertDialog("Error! ${it.message.toString()}")
                try {
                    activity.dismissProgress()
                } catch (e: Exception) {
                }
                return@withContext
            }
        }
        return
    }

    suspend fun handleResponse(errorBody: String?, mydialog: SpotsDialog?, activity: Activity) {

        if (errorBody != null) {
            try {

                val jsonObject = JSONObject(errorBody)

                val details = jsonObject.optString("details")
                val errormessage = when {
                    details.isNotBlank() -> details
                    jsonObject.has("detail") -> jsonObject.getString("detail")
                    jsonObject.has("message") -> jsonObject.getString("message")
                    jsonObject.has("error") -> jsonObject.getString("error")
                    else -> "No error message found"
                }

                try {
                    if (activity.activityisrunning()) {
                        withContext(Dispatchers.Main) {
                            Log.d("-------", "initall: ${errormessage}")
                            activity.showAlertDialog(errormessage.toString())
                        }
                    }
                } catch (e: Exception) {
                    Log.d("-------", "initall: ${errormessage}")
                }


            } catch (e: JSONException) {
                withContext(Dispatchers.Main) {
                    Log.d("-------", "initall: ${e.message.toString()}")
                    activity.showAlertDialog(e.message.toString())
                }
            }

        } else {
            withContext(Dispatchers.Main) {
                activity.showAlertDialog("Error message is null")
            }
        }

    }

    suspend fun getFineUserDetails(activity: Activity): UserFineDetails {
        var myuserfinedetails = UserFineDetails()
        runCatching {
            val response = api.getuserfinedetails(activity.getAuthDetails().access)
            if (response.code() == 401) {
                SessionManager(activity).logout()
                activity.goToActivity(activity, LauncherActivity::class.java)
                return@runCatching
            } else if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                handleResponse(errorBody, null, activity)
                return@runCatching
            } else {
                myuserfinedetails = response.body()!!
                _userfinedetails.postValue(myuserfinedetails)
                val isactive = myuserfinedetails.get(0)?.is_active
                _isParentAccountActive.postValue(isactive)
            }
        }.onFailure {
            networkResponseFailure(it, null, "getFineUserDetails()", activity)
        }
        return myuserfinedetails
    }


    suspend fun getstudentlist(userid: String, activity: Activity): GetStudentResult {
        var myuserfinedetails = GetStudentResult()
        runCatching {
            val response = api.getstudentlist(activity.getAuthDetails().access, userid)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                handleResponse(errorBody, null, activity)
                return@runCatching
            } else {
                myuserfinedetails = response.body()!!
                _studentloggedin.postValue(myuserfinedetails)
            }
        }.onFailure {
            networkResponseFailure(it, null, "getstudentlist()", activity)
        }
        return myuserfinedetails
    }


    suspend fun createCallLog(
        school: String,
        createCallLog: CreateCallLog,
        studentid: Int,
        newstudenttokenbalance: Float?,
        userid: String,
        mobileid: Int?,
        callminutesconsumed: Float,
        tokensused: Float,
        activity: Activity
    ) {
        runCatching {
            val response = api.createCallLog(activity.getAuthDetails().access, createCallLog)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
//                handleResponse(errorBody, null, activity)
                return@runCatching
            } else {
                getStudentDetail(studentid, newstudenttokenbalance!!, activity) //This method updates the token balance
                getstudentlist(userid, activity)
                getFineUserDetails(activity)
                getConstantResults(school, activity)
                if (mobileid != null) {
                    updateMobileMinutesAndToken(mobileid, UpdateMobileBody(callminutesconsumed, tokensused), activity)
                } else {
                    Log.d("-------", "initall: Found mobile to be null")
                }
            }
        }.onFailure {
//            networkResponseFailure(it, null)
        }
    }


    suspend fun getConstantResults(school: String, activity: Activity): GetConstantsResult {
        var theresult = GetConstantsResult()
        runCatching {
            val response = api.getConstants(school)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                handleResponse(errorBody, null, activity)
                return@runCatching
            } else {
                theresult = response.body()!!
                _constants.postValue(theresult)
            }
        }.onFailure {
            networkResponseFailure(it, null, "getConstantResults()", activity)
        }
        return theresult
    }


    suspend fun getStudentDetail(studentid: Int, tokenbalance: Float, activity: Activity) {
        runCatching {
            val response = api.studentDetail(studentid, UpdateTokenBalanceObject(tokenbalance))
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
//                handleResponse(errorBody, null, activity)
                return@runCatching
            } else {
                withContext(Dispatchers.Main) {
                    activity.dismissProgress()
//                    activity.showAlertDialog(response.body()?.details.toString())
                }
            }
        }.onFailure {
//            networkResponseFailure(it, null)
        }
    }


    suspend fun updateMobileMinutesAndToken(mobileid: Int, updatemobilebody: UpdateMobileBody, activity: Activity) {
        runCatching {
            val response = api.getMobiles(mobileid, updatemobilebody)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
//                handleResponse(errorBody, null, activity)
                return@runCatching
            } else {
                withContext(Dispatchers.Main) {
                    activity.dismissProgress()
//                    activity.showAlertDialog(response.body()?.details.toString())
                }
            }
        }.onFailure {
//            networkResponseFailure(it, null)
        }
    }


    suspend fun getUserWithNumber(mobilecalled: String, activity: Activity): UserFineDetails {
        var theresponse = UserFineDetails()
        runCatching {
            val response = api.getUserWithNumber(activity.getAuthDetails().access, mobilecalled)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                handleResponse(errorBody, null, activity)
                return@runCatching
            } else {
                withContext(Dispatchers.Main) {
                    activity.dismissProgress()
                    theresponse = response.body()!!
//                    activity.showAlertDialog(response.body()?.details.toString())
                }
            }
        }.onFailure {
            networkResponseFailure(it, null, "getUserWithNumber()", activity)
        }
        return theresponse
    }


    suspend fun getSchoolDetails(schoolid: String, activity: Activity): SchoolTwo {
        var theresponse = SchoolTwo()
        runCatching {
            val response = api.getSchoolDetails(schoolid)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                handleResponse(errorBody, null, activity)
                return@runCatching
            } else {
                withContext(Dispatchers.Main) {
                    activity.dismissProgress()
                    theresponse = response.body()!!
                }
            }
        }.onFailure {
            networkResponseFailure(it, null, "getSchoolDetails()", activity)
        }
        return theresponse
    }

    suspend fun getStandingTokenForSchool(schoolid: String, activity: Activity): Double {
        var theresponse = 0.0
        runCatching {
            val response = api.getStandingTokenForSchool(schoolid)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                handleResponse(errorBody, null, activity)
                return@runCatching
            } else {
                withContext(Dispatchers.Main) {
                    activity.dismissProgress()
                    theresponse = response.body()!!
                }
            }
        }.onFailure {
            networkResponseFailure(it, null, "getStandingTokenForSchool()", activity)
        }
        return theresponse
    }

    suspend fun getMobileId(schoolid: String, activity: Activity): String {
        var theresponse = ""
        runCatching {
            val response = api.getMobileId(schoolid)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                handleResponse(errorBody, null, activity)
                return@runCatching
            } else {
                withContext(Dispatchers.Main) {
                    activity.dismissProgress()
                    theresponse = response.body()!!
                }
            }
        }.onFailure {
            networkResponseFailure(it, null, "getMobileId()", activity)
        }
        return theresponse
    }

    suspend fun getGlobalSettings(activity: Activity): GetGlobalSettingsItem {
        var theresponse = GetGlobalSettings()
        runCatching {
            val response = api.getGlobalSettings()
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                handleResponse(errorBody, null, activity)
                return@runCatching
            } else {
                withContext(Dispatchers.Main) {
                    activity.dismissProgress()
                    theresponse = response.body()!!
                }
            }
        }.onFailure {
            networkResponseFailure(it, null, "getMobileId()", activity)
        }
        return theresponse.get(0)!!
    }

    suspend fun getDeviceBalance(mobileNumber: String, activity: Activity): GetDeviceBalance {
        var theresponse = GetDeviceBalance(null, null)
        runCatching {
            val response = api.getDeviceBalance(mobileNumber)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                handleResponse(errorBody, null, activity)
                return@runCatching
            } else {
                withContext(Dispatchers.Main) {
                    activity.dismissProgress()
                    theresponse = response.body()!!
                }
            }
        }.onFailure {
            networkResponseFailure(it, null, "getMobileId()", activity)
        }
        return theresponse
    }

    suspend fun getMobile(mobileid: String, activity: Activity): GetMobile {
        var theresponse = GetMobile(null, null, null, null, null, null, null, null)
        runCatching {
            val response = api.getMobile(mobileid)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                handleResponse(errorBody, null, activity)
                return@runCatching
            } else {
                withContext(Dispatchers.Main) {
                    activity.dismissProgress()
                    theresponse = response.body()!!
                }
            }
        }.onFailure {
            networkResponseFailure(it, null, "getDeviceBalance()", activity)
        }
        return theresponse
    }


    private fun saveLoginSessionToStudentId(studentid: Int, activity: Activity) {
        /*val login = Login(studentId = studentid.toString(), loginTimestamp = System.currentTimeMillis(), logoutTimestamp = null)
        CoroutineScope(Dispatchers.IO).launch() {
            val database = RoomDb(activity).loginDao().insert(login)
        }*/
    }


    suspend fun loginuser(email: String, password: String, mydialog: SpotsDialog?, activity: Activity) {

        runCatching {

            val response = api.login(LoginBody(email, password))

            if (!response.isSuccessful) {
                mainScope.launch { activity.dismissProgress() }
                val errorBody = response.errorBody()?.string()
                handleResponse(errorBody, null, activity)
                return@runCatching
            } else {

                withContext(Dispatchers.Main) {

                    val access = response.body()?.access.toString()
                    val refresh = response.body()?.refresh.toString()

                    val map = mutableMapOf<String, String>()
                    map["access"] = access
                    map["refresh"] = refresh

                    SessionManager(activity).savejwtToken(access, refresh)
                    SessionManager(activity).saveUp(email, password)
                    Log.d("-------", "initall: $email,  $password")

                    val _userid = async { getFineUserDetails(activity) }
                    val userid = _userid.await().get(0)!!.id
                    val _studentind = async { getstudentlist(userid, activity) }
                    val studentid = _studentind.await().get(0).id
                    saveLoginSessionToStudentId(studentid, activity)

                    withContext(Dispatchers.Main) {
                        activity.dismissProgress()
                        activity.makeLongToast("Login was successful")
                        activity.goToActivity(activity, TestActivity::class.java)
                    }

                }

            }

        }.onFailure {
            mainScope.launch { activity.dismissProgress() }
            networkResponseFailure(it, mydialog, "loginuser()", activity)
        }

    }


    suspend fun insertSmsDetail(smsDetail: SmsDetail, activity: Activity): SmsDetail {
        val database = RoomDb(activity).getSmsDao()
        var insertedId = ""
        insertedId = database.insertSmsDetail(smsDetail).toString()
        Log.d("Mychek-------", "initall: checking for ${insertedId}")
        return database.getSmsDetailByTimestamp(insertedId.toLong()) // Assuming you have a function to retrieve SmsDetail by ID
    }


    suspend fun insertBatch(listOfSmsDetail: List<SmsDetail>, activity: Activity): UserFineDetails {
        val theresponse = UserFineDetails()
        runCatching {
            val database = RoomDb(activity).getSmsDao()
            database.insertBatch(listOfSmsDetail)
        }.onFailure {
            //networkResponseFailure(it, null, "insertSmsDetail()", activity)
        }
        return theresponse
    }


    suspend fun insertBatchWithRetry(listOfSmsDetail: List<SmsDetail>, activity: Activity) {
        var success = false

        while (!success) {
            runCatching {
                val database = RoomDb(activity).getSmsDao()
                database.insertBatch(listOfSmsDetail)
                // If the code reaches here, the transaction was successful
                success = true
            }.onFailure {
                // Handle the exception or log the error
                success = false
                val message =  it.message ?: "Unknown error occurred"
                Log.d("FailedPut-", "initall: Failed inserting batch ${it.message}")
                // Optionally, you can introduce a delay before retrying
                delay(500L)
            }
        }

    }



    suspend fun insertSmsDetailIgnore(smsDetail: SmsDetail, activity: Activity): UserFineDetails {
        var theresponse = UserFineDetails()
        runCatching {
            val database = RoomDb(activity).getSmsDao()
            database.insertSmsDetailIgnore(smsDetail)
            Log.d("insertSmsDetail-------", "initall: ${smsDetail}")
        }.onFailure {
            //networkResponseFailure(it, null, "insertSmsDetail()", activity)
        }
        return theresponse
    }


    suspend fun getLatestSmsList(activity: Activity): List<SmsDetail> {


        val database = RoomDb(activity).getSmsDao()
        val smslist = database.getLatestSmsList()

        // Remove country code for phone numbers starting with "+254"
        val modifiedSmsList = smslist.map { smsDetail ->
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

        val thelist = uniqueModifiedSmsMap.values.toList()
        return thelist

    }


    /*suspend fun getMessagesByPhoneNumber(phoneNumber: String, activity: Activity): List<SmsDetail> {
        val database = RoomDb(activity).getSmsDao()
        val smslist = database.getMessagesByPhoneNumber(phoneNumber)
        return smslist
    }*/


    suspend fun getMessagesByPhoneNumber(phoneNumber: String, activity: Activity): MutableList<SmsDetail> {
        val normalizedPhoneNumber = normalizePhoneNumber(phoneNumber) // Normalize the phone number
        val database = RoomDb(activity).getSmsDao()

        val smsListOne = database.getMessagesByPhoneNumber(phoneNumber)
        val smsListTwo = database.getMessagesByPhoneNumber(normalizedPhoneNumber)

        return when {
            smsListOne.isNotEmpty() && smsListTwo.isNotEmpty() -> {
                // Both lists have elements, concatenate them and return the sorted list
                (smsListOne + smsListTwo).sortedBy { it.timestamp }.toMutableList()
            }
            smsListOne.isNotEmpty() -> {
                // Only smsListOne has elements, return it
                smsListOne.sortedBy { it.timestamp }.toMutableList()
            }
            smsListTwo.isNotEmpty() -> {
                // Only smsListTwo has elements, return it
                smsListTwo.sortedBy { it.timestamp }.toMutableList()
            }
            else -> {
                // Both lists are empty, return an empty list
                mutableListOf<SmsDetail>()
            }
        }
    }


    private fun normalizePhoneNumber(phoneNumber: String): String {
        // If the phone number starts with "07", add "+254" and remove the leading "0"
        if (phoneNumber.startsWith("07")) {
            return "+254" + phoneNumber.substring(1)
        }

        // If the phone number starts with "+254", remove it and add "0"
        if (phoneNumber.startsWith("+254")) {
            return "0" + phoneNumber.substring(4)
        }

        // For any other cases, return the original number
        return phoneNumber
    }


    suspend fun doesMessageExist(timestamp: Long, activity: Activity): Boolean {
        val database = RoomDb(activity).getSmsDao()
        var count = 0
        count = database.doesMessageExist(timestamp)
        return count > 0
    }


    suspend fun deleteMessageByTimestamp(timestamp: Long, activity: Activity) {
        val database = RoomDb(activity).getSmsDao()
        database.deleteMessageByTimestamp(timestamp)
    }

    suspend fun updateStatusByTimestamp(timestamp: Long, status: String, activity: Activity) {
        val database = RoomDb(activity).getSmsDao()
        database.updateStatusByTimestamp(timestamp, status)
    }


    suspend fun getTotalSmsDetailCount(activity: Activity) : Int {
        return withContext(Dispatchers.IO) {
            val database = RoomDb(activity).getSmsDao()
            database.getTotalSmsDetailCount()
        }
    }

    suspend fun getDraftSmsCount(activity: Activity) : Int{
        return withContext(Dispatchers.IO) {
            val database = RoomDb(activity).getSmsDao()
            database.getDraftSmsCount()
        }
    }

    suspend fun deleteMessagesWithPattern(activity: Activity) {
        val database = RoomDb(activity).getSmsDao()
        database.deleteMessagesWithPattern()
    }


    suspend fun getDraftMessage(activity: Activity): SmsDetail? {
        val database = RoomDb(activity).getSmsDao()
        return database.getDraftMessage()
    }



    // ViewModel method to insert active SIM card
    suspend fun insertActiveSimCard(simCard: SimCard, activity: Activity): Long {
        val database = RoomDb(activity).getSmsDao()
        return database.insertActiveSimCard(simCard)
    }

    // ViewModel method to get active SIM card
    suspend fun getActiveSimCard(activity: Activity): SimCard? {
        val database = RoomDb(activity).getSmsDao()
        return database.getActiveSimCard()
    }


}