package com.propswift.Shared

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.propswift.Activities.LauncherActivity
import com.propswift.Activities.TestActivity
import com.propswift.Dagger.MyApplication
import com.propswift.Retrofit.MyApi
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

    lateinit var activity: Activity

    init {
        activity = (appcontext as MyApplication).currentActivity!!
    }


    interface ActivityCallback {
        fun onDataChanged(data: Any)
    }

    private var activityCallback: ActivityCallback? = null
    fun setActivityCallback(callback: ActivityCallback) {
        activityCallback = callback
    }


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


    suspend fun networkResponseFailure(it: Throwable, mydialog: SpotsDialog?, modelname: String) {
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

    suspend fun handleResponse(errorBody: String?, mydialog: SpotsDialog?) {

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


    suspend fun loginuser(email: String, password: String, mydialog: SpotsDialog?) {

        runCatching {

            val response = api.login(LoginBody(email, password))

            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                handleResponse(errorBody, null)
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

                    withContext(Dispatchers.Main) {
                        activity.makeLongToast("Login was successful")
                        activity.goToActivity(activity, TestActivity::class.java)
                    }

                }

            }

        }.onFailure {
            networkResponseFailure(it, mydialog, "loginuser()")
        }

    }

    suspend fun getFineUserDetails(): UserFineDetails {
        var myuserfinedetails = UserFineDetails()
        runCatching {
            val response = api.getuserfinedetails(activity.getAuthDetails().access)
            if (response.code() == 401) {
                SessionManager(activity).logout()
                activity.goToActivity(activity, LauncherActivity::class.java)
                return@runCatching
            } else if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                handleResponse(errorBody, null)
                return@runCatching
            } else {
                myuserfinedetails = response.body()!!
                _userfinedetails.postValue(myuserfinedetails)
                val isactive = myuserfinedetails.get(0)?.is_active
                _isParentAccountActive.postValue(isactive)
            }
        }.onFailure {
            networkResponseFailure(it, null, "getFineUserDetails()")
        }
        return myuserfinedetails
    }


    suspend fun getstudentlist(userid: String): GetStudentResult {
        var myuserfinedetails = GetStudentResult()
        runCatching {
            val response = api.getstudentlist(activity.getAuthDetails().access, userid)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                handleResponse(errorBody, null)
                return@runCatching
            } else {
                myuserfinedetails = response.body()!!
                _studentloggedin.postValue(myuserfinedetails)
            }
        }.onFailure {
            networkResponseFailure(it, null, "getstudentlist()")
        }
        return myuserfinedetails
    }


    suspend fun createCallLog(school : String, createCallLog: CreateCallLog, studentid: Int, newstudenttokenbalance: Float?, userid: String, mobileid: Int?, callminutesconsumed: Float, tokensused: Float) {
        runCatching {
            val response = api.createCallLog(activity.getAuthDetails().access, createCallLog)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
//                handleResponse(errorBody, null)
                return@runCatching
            } else {
                getStudentDetail(studentid, newstudenttokenbalance!!) //This method updates the token balance
                getstudentlist(userid)
                getFineUserDetails()
                getConstantResults(school)
                if (mobileid != null) {
                    updateMobileMinutesAndToken(mobileid, UpdateMobileBody(callminutesconsumed, tokensused))
                } else {
                    Log.d("-------", "initall: Found mobile to be null")
                }
            }
        }.onFailure {
//            networkResponseFailure(it, null)
        }
    }


    suspend fun getConstantResults(school: String): GetConstantsResult {
        var theresult = GetConstantsResult()
        runCatching {
            val response = api.getConstants(school)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                handleResponse(errorBody, null)
                return@runCatching
            } else {
                theresult = response.body()!!
                _constants.postValue(theresult)
            }
        }.onFailure {
            networkResponseFailure(it, null, "getConstantResults()")
        }
        return theresult
    }


    suspend fun getStudentDetail(studentid: Int, tokenbalance: Float) {
        runCatching {
            val response = api.studentDetail(studentid, UpdateTokenBalanceObject(tokenbalance))
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
//                handleResponse(errorBody, null)
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


    suspend fun updateMobileMinutesAndToken(mobileid: Int, updatemobilebody: UpdateMobileBody) {
        runCatching {
            val response = api.getMobiles(mobileid, updatemobilebody)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
//                handleResponse(errorBody, null)
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


    suspend fun getUserWithNumber(mobilecalled: String): UserFineDetails {
        var theresponse = UserFineDetails()
        runCatching {
            val response = api.getUserWithNumber(activity.getAuthDetails().access, mobilecalled)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                handleResponse(errorBody, null)
                return@runCatching
            } else {
                withContext(Dispatchers.Main) {
                    activity.dismissProgress()
                    theresponse = response.body()!!
//                    activity.showAlertDialog(response.body()?.details.toString())
                }
            }
        }.onFailure {
            networkResponseFailure(it, null, "getUserWithNumber()")
        }
        return theresponse
    }


}