package com.propswift.Shared

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.propswift.Launchers.MainActivity
import com.propswift.Retrofit.Login.MyApi
import dmax.dialog.SpotsDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject


class ViewModel(application: Application, myactivity: Activity) : AndroidViewModel(application) {

    var activity: Activity

    init {
        activity = myactivity
    }


    suspend private fun networkResponseFailure(it: Throwable, mydialog: SpotsDialog?) {

        if (activity.activityisrunning()) {
            withContext(Dispatchers.Main) {
//                activity.dismiss(mydialog!!)
                Log.d("----------", "networkResponseFailure: KERROR - ${it.message}")
//                activity.makeLongToast("Error! ${it.message.toString()}")
                activity.showAlertDialog("Error! ${it.message.toString()}")
            }
        }
        return
    }

    private suspend fun handleResponse(jsonObj: JSONObject, responseString: String, mydialog: SpotsDialog?) {
        if (jsonObj.has("details")) {
            val message = jsonObj.getString("details")
            if (activity.activityisrunning()) {
                withContext(Dispatchers.Main) {
                    activity.dismiss(mydialog!!)
                    activity.showAlertDialog(message)
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                activity.dismiss(mydialog!!)
                activity.showAlertDialog(responseString)
                Log.d("----------", "networkResponseFailure: KERROR - ${responseString}")
            }
        }
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

                SessionManager(activity).saveAuthToken(authtoken.toString(), refreshtoken.toString(), jwttoken.toString())
                SessionManager(activity).saveUp(email, password)

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


    suspend fun loginuser_register(email: String, password: String, mobile: String, mydialog: SpotsDialog) {

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

                SessionManager(activity).saveAuthToken(authtoken.toString(), refreshtoken.toString(), jwttoken.toString())
                SessionManager(activity).saveUp(email, password)

                Log.d("-------", "initall: $email,  $password")

                withContext(Dispatchers.Main) {
                    val intent = Intent(activity, MainActivity::class.java)
                    intent.putExtra("mobile", mobile)
                    activity.startActivity(intent)
                    activity.finish()
                }

            }

        }.onFailure {
            networkResponseFailure(it, mydialog)
        }
    }

    suspend fun registeruser(email: String, first_name: String, last_name: String, middle_name: String, username: String, password: String, confirm_password: String, mydialog: SpotsDialog) {
        val user = User(email, first_name, last_name, middle_name, username, password, confirm_password)
        runCatching {
            val response = MyApi().register(user)
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), mydialog)
                return
            } else {
                loginuser_register(email, password, "0729836000", mydialog)
                SessionManager(activity).saveUp(email, password)
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
                SessionManager(activity).saveAuthToken(authtoken.toString(), refreshtoken.toString(), jwttoken.toString())
            }

        }.onFailure {
            /*500*/
            networkResponseFailure(it, null)
        }
    }


    suspend fun getrentedproperties(): RentedProperties {
        var rentedList = RentedProperties(null)
        runCatching {
            val response = MyApi().getrentedproperties(activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken)
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                rentedList = response.body()!!
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return rentedList
    }


    suspend fun getOwnedproperties(): OwnedProperties {
        var rentedList = OwnedProperties(null)
        runCatching {
            val response = MyApi().getownedproperties("Bearer 5Em0USW0TqIuPBWsfRQRmmxsG1k0bR", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJ1c2VyIjoiZjg3OTJiMGEtMDg2YS00ZGNmLWI5ZWQtYTg4MzgwMTA2MzM4Iiwicm9sZXMiOltdLCJleHAiOjE2NzQ4ODYxMzcsImlhdCI6MTY3NDc5OTczNywiYXVkIjoidXJuOmpzdCJ9.Ugfg-vcyK00PUeVFON6thCrU2rNxosX4m5gXRDWAlPZeL2ApF05DBg8Ky571L2QWwvbGrJzH9ifg-EvKbckT9g")
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                rentedList = response.body()!!
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return rentedList
    }


    suspend fun getpaidrentAll(): RentObject {
        var rentedList = RentObject(null)
        runCatching {
            val response = MyApi().getpaidrentAll( "paid", activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken)
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                rentedList = response.body()!!
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return rentedList
    }


    suspend fun getunpaidrentAll(): RentObject {
        var rentedList = RentObject(null)
        runCatching {
            val response = MyApi().getunpaidrentAll( "unpaid", activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken)
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                rentedList = response.body()!!
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return rentedList
    }



    suspend fun getExpensesGeneralAll(): ExpenseObject {
        var expenseList = ExpenseObject(null)
        runCatching {
            val response = MyApi().getExpensesGeneralAll( "general","Bearer 5Em0USW0TqIuPBWsfRQRmmxsG1k0bR", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJ1c2VyIjoiZjg3OTJiMGEtMDg2YS00ZGNmLWI5ZWQtYTg4MzgwMTA2MzM4Iiwicm9sZXMiOltdLCJleHAiOjE2NzQ4ODYxMzcsImlhdCI6MTY3NDc5OTczNywiYXVkIjoidXJuOmpzdCJ9.Ugfg-vcyK00PUeVFON6thCrU2rNxosX4m5gXRDWAlPZeL2ApF05DBg8Ky571L2QWwvbGrJzH9ifg-EvKbckT9g")
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                expenseList = response.body()!!
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return expenseList
    }




}