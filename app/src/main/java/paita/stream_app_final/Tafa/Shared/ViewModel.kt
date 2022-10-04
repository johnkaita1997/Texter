package paita.stream_app_final.Tafa.Shared

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import android.widget.Spinner
import androidx.lifecycle.AndroidViewModel
import dmax.dialog.SpotsDialog
import paita.stream_app_final.AppConstants.Constants
import paita.stream_app_final.Extensions.*
import paita.stream_app_final.Tafa.Activities.MainActivity
import paita.stream_app_final.Tafa.Adapters.*
import paita.stream_app_final.Tafa.Retrofit.Login.MyApi
import kotlinx.coroutines.*
import org.json.JSONObject
import paita.stream_app_final.Tafa.Activities.ConfirmOtpActivity

class ViewModel(application: Application, myactivity: Activity) : AndroidViewModel(application) {

    var activity: Activity

    init {
        activity = myactivity
    }

    suspend private fun networkResponseFailure(it: Throwable, mydialog: SpotsDialog?) {

        if (activity.activityisrunning()) {
            withContext(Dispatchers.Main) {
                if (mydialog != null) if (mydialog.isShowing) mydialog.dismiss()
                activity.makeLongToast("Error! ${it.message.toString()}")
            }
        }
        return
    }

    private suspend fun handleResponse(jsonObj: JSONObject, responseString: String, mydialog: SpotsDialog?) {
        if (jsonObj.has("details")) {
            val message = jsonObj.getString("details")
            if (activity.activityisrunning()) {
                withContext(Dispatchers.Main) {
                    if (mydialog != null) if (mydialog.isShowing) mydialog.dismiss()
                    activity.showAlertDialog(message)
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                if (mydialog != null) if (mydialog.isShowing) mydialog.dismiss()
                activity.showAlertDialog(responseString)
            }
        }
    }

    private suspend fun handleResponseVidocipher(jsonObj: JSONObject, responseString: String) {
        if (jsonObj.has("message")) {
            val message = jsonObj.getString("message")
            if (activity.activityisrunning()) {
                withContext(Dispatchers.Main) {
                    activity.showAlertDialog(message)
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                activity.showAlertDialog(responseString)
            }
        }
    }

    suspend fun getCounties(): List<MyDetail> {

        val thelist = mutableListOf<MyDetail>()

        val job = CoroutineScope(Dispatchers.IO).launch(activity.coroutineexception(activity)) {
            val response = MyApi().getcounties()
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@launch
            } else {
                response.body()?.details?.forEach {
                    thelist.add(MyDetail(it.id, it.name, it.code, it.users))
                }
            }
        }
        job.join()
        return thelist

    }

    suspend fun getForms(): MyForms {

        val myobject = MyForms()

        runCatching {
            val response = MyApi().getForms()
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                response.body()?.forEach {
                    myobject.add(it)
                }
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return myobject

    }

    suspend fun isFormSubscribed(formid: String): Boolean {
        var mystatus = false
        runCatching {
            val status = MyApi().checkFormSubscription(FormActive(formid), activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken).code()
            mystatus = status == 200
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return mystatus
    }

    suspend fun isSubjectsubscribed(formid: String, subjectid: String): Boolean {
        var mystatus = false
        runCatching {
            val status = MyApi().checkSubjectSubscription(SubjectActive(formid, subjectid), activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken).code()
            if (status == 200) mystatus = true else if (status == 400) mystatus = false
            mystatus = status == 200
        }
        return mystatus
    }

    suspend fun getRegCodes(): MyCounty {
        lateinit var mycounty: MyCounty

        runCatching {
            val response = MyApi().getagents()
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                mycounty = response.body()!!
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }

        return mycounty
    }

    suspend fun getFormAmounts(formid: String): FormAmount {

        var myformamountlist = FormAmount()

        runCatching {
            val response = MyApi().getFormAmounts(formid)
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                myformamountlist = response.body()!!
                Log.d("------------", "getFormAmounts: 111111")
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }

        return myformamountlist

    }

    suspend fun checkoutForm(checkoutformobject: CheckoutForm): String {

        var invoiceId = ""

        runCatching {
            val response = MyApi().checkoutform(checkoutformobject)
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                invoiceId = response.body()?.details.toString()
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }

        return invoiceId

    }

    suspend fun getSubjects(formid: String): RetroSubjects {
        var subjectlist = RetroSubjects()

        runCatching {
            val response = MyApi().fetchSubjects(formid)
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                subjectlist = response.body()!!
            }

        }.onFailure {
            networkResponseFailure(it, null)
        }
        return subjectlist
    }

    suspend fun getsubjectplanlist(formid: String, subjectid: String): SubjectPlanList {
        var subjectplanlist = SubjectPlanList()

        runCatching {
            val response = MyApi().getsubjectplanlist(formid, subjectid)
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                subjectplanlist = response.body()!!
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return subjectplanlist
    }

    suspend fun checkoutSubject(checkoutsubjectobject: CheckOutSubject): String {
        var invoiceId = ""
        runCatching {
            val response = MyApi().checkoutsubject(checkoutsubjectobject)
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                invoiceId = response.body()?.details.toString()
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return invoiceId
    }

    suspend fun getListOfUnits(formid: String, subjectid: String): MyUnit {
        var theunitlist = MyUnit()

        runCatching {
            val response = MyApi().fetchlistofUnits(formid, subjectid)
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                theunitlist = response.body()!!
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return theunitlist
    }

    suspend fun fetchvideosperunitname(unitid: String): Videosperunitname {
        var myvideosperunitname = Videosperunitname(listOf())
        runCatching {
            val response = MyApi().fetchvideosperunitname(unitid)
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                myvideosperunitname = response.body()!!
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return myvideosperunitname
    }

    suspend fun isUnitSubscribed(unitid: String): Boolean {
        var mystatus = false
        runCatching {
            val status = MyApi().checkunitsubscriptionStatus(unitid, activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken).code()
            mystatus = status == 200
        }.onFailure {
            networkResponseFailure(it, null)
        }

        return mystatus
    }

    suspend fun getUnitPrices(unitid: String): UnitPricesObject {
        var pricelist = UnitPricesObject()
        runCatching {
            val response = MyApi().getunitprices(unitid, activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken)
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                pricelist = response.body()!!
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return pricelist
    }

    suspend fun getPlaybackInfo(videoid: String): VidToken {
        var pricelist = VidToken("", "")
        runCatching {
            val response = MyApi().gettokens(Constants.apisecret, videoid)
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponseVidocipher(jsonObj, response.toString())
                return@runCatching
            } else {
                pricelist = response.body()!!
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return pricelist
    }

    suspend fun checkoutUnit(checkoutunitobject: CheckOutUnit): String {
        var invoiceId = ""
        runCatching {
            val response = MyApi().checkoutUnit(checkoutunitobject)
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                invoiceId = response.body()?.details.toString()
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return invoiceId
    }

    suspend fun checkoutTopic(checkoutTopicObject: CheckOutTopic): String {
        var invoiceId = ""
        runCatching {
            val response = MyApi().checkoutTopic(checkoutTopicObject)
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                invoiceId = response.body()?.details.toString()
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return invoiceId
    }


    suspend fun fetch_Subject_Lists(formid: String): RetroSubjects {

        var retrosubject = RetroSubjects()

        runCatching {
            val response = MyApi().fetchSubjects(formid)
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                retrosubject = response.body()!!
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return retrosubject
    }



    suspend fun loginuser(email: String, password: String, mydialog: SpotsDialog) {

        runCatching {

            val response = MyApi().login(LoginBody(email, password))

            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), mydialog)
                return@runCatching
            } else {

                val authtoken = response.body()?.details?.access_token.toString()
                val expires_in = response.body()?.details?.expires_in.toString()
                val jwttoken = response.body()?.details?.jwt_token.toString()
                val refreshtoken = response.body()?.details?.refresh_token.toString()

                val map = mutableMapOf<String, String>()
                map["refreshtoken"] = refreshtoken
                map["jwttoken"] = jwttoken
                map["authtoken"] = authtoken

                activity.sessionManager().saveAuthToken(authtoken.toString(), refreshtoken.toString(), jwttoken.toString())
                activity.sessionManager().saveUp(email, password)

                Log.d("-------", "initall: $email,  $password")

                withContext(Dispatchers.Main) {

                    activity.makeLongToast("Login was successful")
                    val intent = Intent(activity, MainActivity::class.java)
                    activity.startActivity(intent)
                    activity.finish()


                }

            }

        }.onFailure {
            networkResponseFailure(it, mydialog)
        }

    }




    suspend fun loginuser_register(email: String, password: String, mobile : String, mydialog: SpotsDialog) {

        runCatching {
            val response = MyApi().login(LoginBody(email, password))

            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), mydialog)
                return@runCatching
            } else {

                val authtoken = response.body()?.details?.access_token.toString()
                val expires_in = response.body()?.details?.expires_in.toString()
                val jwttoken = response.body()?.details?.jwt_token.toString()
                val refreshtoken = response.body()?.details?.refresh_token.toString()

                val map = mutableMapOf<String, String>()
                map["refreshtoken"] = refreshtoken
                map["jwttoken"] = jwttoken
                map["authtoken"] = authtoken

                activity.sessionManager().saveAuthToken(authtoken.toString(), refreshtoken.toString(), jwttoken.toString())
                activity.sessionManager().saveUp(email, password)

                Log.d("-------", "initall: $email,  $password")

                withContext(Dispatchers.Main) {
                    val intent = Intent(activity, ConfirmOtpActivity::class.java)
                    intent.putExtra("mobile", mobile)
                    activity.startActivity(intent)
                    activity.finish()
                }

            }

        }.onFailure {
            networkResponseFailure(it, mydialog)
        }
    }

    suspend fun createUser (email: String, first_name: String, last_name: String, phone: String, password: String, confirm_password: String, county: String, code: String, school: String, mydialog: SpotsDialog) {
        val user = User(email, first_name, last_name, phone, password, confirm_password, county, code, school)
        runCatching {
            val response = MyApi().register(user)
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), mydialog)
                return
            } else {
                loginuser_register(email, password, phone, mydialog)
                activity.sessionManager().saveUp(email, password)
            }
        }.onFailure {
            networkResponseFailure(it, mydialog)
        }
    }

    suspend fun refreshtoken(e: String, p: String) {

        runCatching {
            val response = MyApi().login(LoginBody(e, p))
            if (!response.isSuccessful) {
                return@runCatching
            } else {

                val authtoken = response.body()?.details?.access_token.toString()
                val expires_in = response.body()?.details?.expires_in.toString()
                val jwttoken = response.body()?.details?.jwt_token.toString()
                val refreshtoken = response.body()?.details?.refresh_token.toString()

                val map = mutableMapOf<String, String>()
                map["refreshtoken"] = refreshtoken
                map["jwttoken"] = jwttoken
                map["authtoken"] = authtoken
                activity.sessionManager().saveAuthToken(authtoken.toString(), refreshtoken.toString(), jwttoken.toString())
            }

        }.onFailure {
            /*500*/
            networkResponseFailure(it, null)
        }


    }

    suspend fun fetchAndSaveCounties(spinnerCounty: Spinner) {

        runCatching {
            val response = MyApi().getcounties()
            withContext(Dispatchers.Main) {
                if (!response.isSuccessful) {
                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    handleResponse(jsonObj, response.toString(), null)
                } else {

                    withContext(Dispatchers.Main) {
                        val mylist = mutableListOf<String>()
                        response.body()?.details?.forEach {
                            mylist.add(it.name)
                        }
                        activity.populateSpinner(spinnerCounty, mylist)
                    }
                }
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }

    }

    suspend fun fetchAndSaveAgents(spinneragent: Spinner) {

        runCatching {

            val response = MyApi().getagents()
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
            } else {

                val mylist = mutableListOf<String>()
                response.body()?.details?.forEach {
                    mylist.add(it.name)
                }

                withContext(Dispatchers.Main) {
                    activity.populateSpinner(spinneragent, mylist)
                }

            }


        }.onFailure {
            networkResponseFailure(it, null)
        }

    }


    suspend fun topicAmounts(formid: String, subjectid: String): TopicAmounts {

        var topicAmount = TopicAmounts()

        runCatching {
            val response = MyApi().topicamount(formid)
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                topicAmount = response.body()!!
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return topicAmount

    }


    suspend fun fetchTopicList(formid: String, subjectid: String): Topics {

        var topiclist = Topics()

        runCatching {
            val response = MyApi().listoftopics(formid, subjectid)
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                topiclist = response.body()!!
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return topiclist
    }


    suspend fun checkTopicSubscription(topicid: String): Boolean {
        var mystatus = false
        runCatching {
            val thetopicId = mutableMapOf<String, String>()
            thetopicId["topic"] = topicid
            Log.d("-------", "checkTopicSubscription: SEnding ${thetopicId}")
            val response = MyApi().checkTopicSubscription(thetopicId, activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken).code()
            mystatus = response == 200
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return mystatus
    }


    suspend fun getYourVideos(formid: String): YourVideos {
        var theyourVideos = YourVideos(ArrayList<YoursDetail>())
        runCatching {
            val response = MyApi().getYourVideos(formid, activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken)
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                theyourVideos = response.body()!!
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return theyourVideos
    }


    suspend fun confirmOTP(confirmcode: String, mobileNumber: String):Boolean{
        var mystatus = false
        runCatching {
            val status = MyApi().verifyOtp(VerifyOtp(confirmcode, mobileNumber)).code()
            mystatus = status == 200
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return mystatus
    }


    suspend fun getUserProfileDetails():UserProfileDetails{
        var userProfileDetails = UserProfileDetails(null)
        runCatching {
            Log.d("-----------------------------", "getUserProfileDetails: ${SessionManager(activity).fetchAuthToken()}")
            var response = MyApi().getUserDetails(SessionManager(activity).fetchAuthToken(), SessionManager(activity).fetchJwtToken())
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                userProfileDetails = response.body()!!
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return userProfileDetails
    }


    suspend fun getTransactions():Transactions{
        var usertransactions = Transactions(null)
        runCatching {
            var response = MyApi().getTransactions(SessionManager(activity).fetchAuthToken(), SessionManager(activity).fetchJwtToken())
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                usertransactions = response.body()!!
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return usertransactions
    }


    suspend fun getFreeVideos(subjectid: String):FreeVideos{
        var freeVideos = FreeVideos(null)
        runCatching {
            var response = MyApi().getFreeVideos(subjectid)
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                freeVideos = response.body()!!
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return freeVideos
    }






}