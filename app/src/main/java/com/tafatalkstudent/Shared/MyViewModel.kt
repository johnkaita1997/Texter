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


    suspend fun createCallLog(school : String, createCallLog: CreateCallLog, studentid: Int, newstudenttokenbalance: Float?, userid: String, mobileid: Int?, callminutesconsumed: Float, tokensused: Float, activity: Activity) {
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


    private fun saveLoginSessionToStudentId(studentid: Int, activity:Activity) {
        val login = Login(studentId = studentid.toString(), loginTimestamp = System.currentTimeMillis(), logoutTimestamp = null)
        CoroutineScope(Dispatchers.IO).launch() {
            val database = RoomDb(activity).loginDao().insert(login)
        }
    }


    suspend fun loginuser(email: String, password: String, mydialog: SpotsDialog?, activity: Activity) {

        runCatching {

            val response = api.login(LoginBody(email, password))

            if (!response.isSuccessful) {
                mainScope.launch { activity.dismissProgress()}
                val errorBody = response.errorBody()?.string()
                handleResponse(errorBody, null, activity)
                return@runCatching
            } else {

                withContext(Dispatchers.Main){

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
            mainScope.launch { activity.dismissProgress()}
            networkResponseFailure(it, mydialog, "loginuser()", activity)
        }

    }


}