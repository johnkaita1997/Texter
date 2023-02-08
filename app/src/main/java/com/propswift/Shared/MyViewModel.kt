package com.propswift.Shared

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.LinearLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.propswift.Activities.MainActivity
import com.propswift.Dagger.MyApplication
import com.propswift.Retrofit.MyApi
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dmax.dialog.SpotsDialog
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Named
import kotlin.collections.set
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class MyViewModel
@Inject constructor(
    @Named("myapi") private val api: MyApi,
    @ApplicationContext private val appcontext: Context
) : ViewModel() {

    lateinit var activity: Activity

    init {
        activity = (appcontext as MyApplication).currentActivity!!
    }


    val _listOfTodoItems = MutableLiveData<MutableList<GetToDoListTasks_Details>>()
    val listOfTodoItems: LiveData<MutableList<GetToDoListTasks_Details>> get() = _listOfTodoItems

    val _bothNames = MutableLiveData<String>()
    val bothNames: LiveData<String> get() = _bothNames

    val _totalAmount = MutableLiveData<String>()
    val totalAmount: LiveData<String> get() = _totalAmount

    val _getTotalNumberofReceipts = MutableLiveData<String>()
    val getTotalNumberofReceipts: LiveData<String> get() = _getTotalNumberofReceipts

    val _listRentals = MutableLiveData<MutableList<RentDetail>>()
    val listRentals: LiveData<MutableList<RentDetail>> get() = _listRentals

    val _getExpenses = MutableLiveData<MutableList<FetchExpenseObject_Detail>>()
    val getExpenses: LiveData<MutableList<FetchExpenseObject_Detail>> get() = _getExpenses

    val _listOfOwnedProperties = MutableLiveData< MutableList<OwnedDetail>?>()
    val listOfOwnedProperties: LiveData< MutableList<OwnedDetail>?> get() = _listOfOwnedProperties

    val _listofRentedProperties = MutableLiveData< MutableList<RentedDetail>?>()
    val listofRentedProperties: LiveData< MutableList<RentedDetail>?> get() = _listofRentedProperties


    val is_manager = false


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

            val response = api.login(LoginBody(email, password))

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
            val response = api.login(LoginBody(email, password))
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
            val response = api.register(user)
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
            val response = api.login(LoginBody(e, p))
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


    suspend fun getrentedproperties() {
        runCatching {
            val response = api.getrentedproperties(
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                val rentedList = response.body()!!.details
                _listofRentedProperties.postValue(rentedList as MutableList<RentedDetail>?)
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
    }

    suspend fun getOwnedproperties() {
        runCatching {
            val response = api.getownedproperties(
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                val ownedList = response.body()!!.details
                _listOfOwnedProperties.postValue(ownedList as MutableList<OwnedDetail>?)
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }.onSuccess {
            withContext(Dispatchers.Main) {
//                activity.showAlertDialog(rentedList.details.toString())
            }
        }
    }


    suspend fun getRentals(rentFilter: RentFilter) {
        runCatching {
            val response = api.getRentals(
                rentFilter.filter, rentFilter.property_id, rentFilter.date_from, rentFilter.date_to, activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                val rentedList = response.body()!!.details!!
                _listRentals.postValue(rentedList as MutableList<RentDetail>?)
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
    }


    suspend fun getunpaidrentAll(): RentalObject {
        var rentedList = RentalObject(null)
        runCatching {
            val response = api.getRentals(
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
    suspend fun getExpenses(expenseFilter: ExpenseFilter) {
        runCatching {
            val response = api.getExpenses(
                expenseFilter.filter, expenseFilter.property_id, expenseFilter.date_from, expenseFilter.date_to, activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                val expenseList = response.body()!!.details!!
                _getExpenses.postValue(expenseList as MutableList<FetchExpenseObject_Detail>?)
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
    }

    suspend fun createProperty(property: CreateProperty) {
        runCatching {
            val response = api.createProperty(
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
            val response = api.getUserDetails(
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
            val response = api.getPropertyManagers(
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
            val response = api.addManager(
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
            val response = api.removeManager(
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
            val response = api.getManagedPropertiesForManager(
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


    suspend fun addToDoList(toDoList: ToDoListTask, viewModel: MyViewModel) {
        runCatching {
            val response = api.addToDoList(
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


    suspend fun removeToDoList(itemId: String) {
        runCatching {
            val response = api.removeToDoList(
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken, RemoveToDoId(itemId)
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return
            } else {
                withContext(Dispatchers.Main) {
                    activity.showAlertDialog("Todo List was removed successfully")
                }
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
    }


    suspend fun getUserProfileDetails() {
        runCatching {
            val response = api.getUserProfileDetails(activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken)
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                val getUserProfileDetails = response.body()!!.details

                val myusername = getUserProfileDetails!!.username
                val myuserid = getUserProfileDetails.user_id
                val myfirstname = getUserProfileDetails.first_name
                val mymiddle_name = getUserProfileDetails.middle_name
                val mylast_name = getUserProfileDetails.last_name
                val myis_manager = false
                _bothNames.postValue("$myfirstname ${mylast_name}")

            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
    }


    suspend fun uploadFile(filemap: MutableList<MultipartBody.Part>): List<String> {

        var imagelist = listOf<String>()
        runCatching {
            val response = api.uploadImage(
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


    suspend fun addExpense(expense: ExpenseUploadObject, root: LinearLayout) {
        runCatching {
            val response = api.addExpense(
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken, expense
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return
            } else {
                clearAllEditTexts(root)
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
    }


    suspend fun getTotal() {
        runCatching {
            val response = api.getTotalSpent(
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                val total = response.body()!!.details
                _totalAmount.postValue(total.toString())
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
    }

    suspend fun getTotalNumberofReceipts() {
        runCatching {
            val response = api.getTotalNumberofReceipts(
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                val total = response.body()!!.details
                _getTotalNumberofReceipts.postValue(total.toString())
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
    }


    suspend fun getToDoList() {
        runCatching {
            val response = api.getToDoList(
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                withContext(Dispatchers.Main) {
                    _listOfTodoItems.value = response.body()!!.details!!
                }
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
    }


}