package com.propswift.Shared

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.propswift.Activities.MainActivity
import com.propswift.Retrofit.MyApi
import dmax.dialog.SpotsDialog
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import org.json.JSONObject
import kotlin.coroutines.CoroutineContext


class ViewModel(application: Application, myactivity: Activity) : AndroidViewModel(application) {

    var activity: Activity

    init {
        activity = myactivity
    }

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


    suspend fun networkResponseFailure(it: Throwable, mydialog: SpotsDialog?) {

        if (activity.activityisrunning()) {
            withContext(Dispatchers.Main) {

//                activity.dismiss(mydialog!!)
                Log.d("----------", "networkResponseFailure: KERROR - ${it.message}")
//                activity.makeLongToast("Error! ${it.message.toString()}")
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

    suspend fun handleResponse(
        jsonObj: JSONObject, responseString: String, mydialog: SpotsDialog?
    ) {
        if (jsonObj.has("details")) {
            val message = jsonObj.getString("details")
            if (activity.activityisrunning()) {
                withContext(Dispatchers.Main) {
                    activity.dismiss(mydialog!!)
                    activity.showAlertDialog(message)
                    activity.dismissProgress()
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

                SessionManager(activity).saveAuthToken(
                    authtoken.toString(), refreshtoken.toString(), jwttoken.toString()
                )
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


    suspend fun loginuser_register(
        email: String, password: String, mobile: String, mydialog: SpotsDialog
    ) {

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

                SessionManager(activity).saveAuthToken(
                    authtoken.toString(), refreshtoken.toString(), jwttoken.toString()
                )
                SessionManager(activity).saveUp(email, password)

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

    suspend fun registeruser(
        email: String, first_name: String, last_name: String, middle_name: String, username: String, password: String, confirm_password: String, mydialog: SpotsDialog
    ) {
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
                SessionManager(activity).saveAuthToken(
                    authtoken.toString(), refreshtoken.toString(), jwttoken.toString()
                )
            }

        }.onFailure {
            /*500*/
            networkResponseFailure(it, null)
        }
    }


    suspend fun getrentedproperties(): RentedProperties {
        Log.d("-------", "initall: started rented")
        var rentedList = RentedProperties(null)
        runCatching {
            val response = MyApi().getrentedproperties(
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken
            )
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
        Log.d("-------", "initall: started owned")
        var rentedList = OwnedProperties(null)
        runCatching {
            val response = MyApi().getownedproperties(
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                rentedList = response.body()!!
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }.onSuccess {
            withContext(Dispatchers.Main) {
//                activity.showAlertDialog(rentedList.details.toString())
            }
        }
        return rentedList
    }


    suspend fun getRentals(rentFilter: RentFilter): List<RentDetail> {
        var rentedList = listOf<RentDetail>()
        runCatching {
            val response = MyApi().getRentals(
                rentFilter.filter, rentFilter.property_id, rentFilter.date_from, rentFilter.date_to, activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                rentedList = response.body()!!.details!!
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return rentedList
    }


    suspend fun getunpaidrentAll(): RentalObject {
        var rentedList = RentalObject(null)
        runCatching {
            val response = MyApi().getRentals(
                "unpaid", null, null, null, activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken
            )
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


    //comment
    suspend fun getExpenses(expenseFilter: ExpenseFilter): List<FetchExpenseObject_Detail> {
        var expenseList = listOf<FetchExpenseObject_Detail>()
        runCatching {
            val response = MyApi().getExpenses(
                expenseFilter.filter, expenseFilter.property_id, expenseFilter.date_from, expenseFilter.date_to, activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                expenseList = response.body()!!.details!!
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return expenseList
    }

    suspend fun createProperty(property: CreateProperty) {
        runCatching {
            val response = MyApi().createProperty(
                property, activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return
            } else {
                withContext(Dispatchers.Main) {
                    activity.dismissProgress()
                    activity.showAlertDialog("Property was created successfully")
                }
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
    }


    suspend fun getUserDetails(): UserDetails {
        var userDetails = UserDetails(null)
        runCatching {
            val userid = "f8792b0a-086a-4dcf-b9ed-a88380106338"
            val response = MyApi().getUserDetails(
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken, userid
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                userDetails = response.body()!!
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return userDetails
    }


    suspend fun getPropertyManagers(propertyid: String): MutableList<GetPropertyManagerDetails_Details> {
        var getPropertyManagers = mutableListOf<GetPropertyManagerDetails_Details>()
        runCatching {
            val response = MyApi().getPropertyManagers(
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken, propertyid
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                getPropertyManagers = response.body()!!.details
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return getPropertyManagers
    }


    suspend fun addManager(manager: Manager) {
        runCatching {
            val response = MyApi().addManager(
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken, manager
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return
            } else {
                withContext(Dispatchers.Main) {
                    activity.showAlertDialog("Manager was added successfully")
                }
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
    }


    suspend fun removeManager(managerId: String, propertyId: String) {
        runCatching {
            val response = MyApi().removeManager(
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken, RemoveManager(managerId, propertyId)
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return
            } else {
//                activity.showAlertDialog("Manager was removed successfully")
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
    }


    suspend fun getManagedPropertyForManager(): MutableList<ListOfManagedProperties_Detail> {
        var getManagedPropertyForManager = mutableListOf<ListOfManagedProperties_Detail>()
        runCatching {
            val response = MyApi().getManagedPropertiesForManager(
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                getManagedPropertyForManager = response.body()!!
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return getManagedPropertyForManager
    }


    suspend fun addToDoList(toDoList: ToDoListTask) {
        runCatching {
            val response = MyApi().addToDoList(
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken, toDoList
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return
            } else {
                withContext(Dispatchers.Main) {
                    activity.showAlertDialog("Manager was added successfully")
                }
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
    }


    suspend fun getToDoList(): MutableList<GetToDoListTasks_Details> {
        var getToDoListItems = mutableListOf<GetToDoListTasks_Details>()
        runCatching {
            val response = MyApi().getToDoList(
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                getToDoListItems = response.body()!!.details!!
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return getToDoListItems
    }


    suspend fun removeToDoList(itemId: String) {
        runCatching {
            val response = MyApi().removeToDoList(
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken, RemoveToDoId(itemId)
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return
            } else {
                withContext(Dispatchers.Main) {
                    activity.showAlertDialog("Manager was removed successfully")
                }
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
    }


    suspend fun getUserProfileDetails(): GetProfileDetails {
        var getUserProfileDetails = GetProfileDetails(null)
        runCatching {
            val response = MyApi().getUserProfileDetails(
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                getUserProfileDetails = response.body()!!
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return getUserProfileDetails
    }


    suspend fun uploadFile(filemap: MutableList<MultipartBody.Part>) : List<String> {

        var imagelist = listOf<String>()
        runCatching {
            val response = MyApi().uploadImage(
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken, filemap
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                withContext(Dispatchers.Main) {
                    activity.makeLongToast("Image Upload Was SuccessFul")
                     imagelist = response.body()!!.details
                }
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return imagelist
    }


    suspend fun addExpense(expense: ExpenseUploadObject) {
        runCatching {
            val response = MyApi().addExpense(
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken, expense
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return
            } else {

            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
    }




    suspend fun getTotal(): Total {
        var total = Total(null)
        runCatching {
            val response = MyApi().getTotalSpent(
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                total = response.body()!!
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return total
    }

    suspend fun getTotalNumberofReceipts(): Total {
        var total = Total(null)
        runCatching {
            val response = MyApi().getTotalNumberofReceipts(
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                total = response.body()!!
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
        return total
    }


}