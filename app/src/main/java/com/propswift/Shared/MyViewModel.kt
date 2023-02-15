package com.propswift.Shared

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.propswift.Activities.MainActivity
import com.propswift.Dagger.MyApplication
import com.propswift.Managers.ManagedProperties.ManagersPropertiesList
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


    val _listOfTodoItems = MutableLiveData<MutableList<GetToDoListTasks_Details>>()
    val listOfTodoItems: LiveData<MutableList<GetToDoListTasks_Details>> get() = _listOfTodoItems

    val _listOfTodoItemsDueToday = MutableLiveData<MutableList<GetToDoListTasks_Details>>()
    val listOfTodoItemsDueToday: LiveData<MutableList<GetToDoListTasks_Details>> get() = _listOfTodoItemsDueToday

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

    val _listOfOwnedProperties = MutableLiveData<MutableList<OwnedDetail>?>()
    val listOfOwnedProperties: LiveData<MutableList<OwnedDetail>?> get() = _listOfOwnedProperties

    val _listofRentedProperties = MutableLiveData<MutableList<RentedDetail>?>()
    val listofRentedProperties: LiveData<MutableList<RentedDetail>?> get() = _listofRentedProperties

    val _isManager = MutableLiveData<Boolean?>()
    val isManager: MutableLiveData<Boolean?> get() = _isManager

    val _getOtherReceipts = MutableLiveData<MutableList<OtherReceiptCallbackDetails>>()
    val getOtherReceipts: LiveData<MutableList<OtherReceiptCallbackDetails>> get() = _getOtherReceipts

    val _listManagedProperties = MutableLiveData<MutableList<ListManagedPropertiesDetail>?>()
    val listManagedProperties: LiveData<MutableList<ListManagedPropertiesDetail>?> get() = _listManagedProperties

    val _listallproperties = MutableLiveData<MutableList<ListManagedPropertiesDetail>?>()
    val listallproperties: LiveData<MutableList<ListManagedPropertiesDetail>?> get() = _listallproperties

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
                Log.d("----------", "networkResponseFailure: KERROR - ${it.message}")
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

    suspend fun handleResponse(jsonObj: JSONObject, responseString: String, mydialog: SpotsDialog?) {
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
                    managerCheck()
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
                managerCheck()
            }

        }.onFailure {
            networkResponseFailure(it, mydialog)
        }
    }

    suspend fun registeruser(
        email: String, first_name: String, last_name: String, middle_name: String, username: String, password: String, confirm_password: String, mydialog: SpotsDialog
    ) {
        val user = User(email, first_name, last_name, middle_name, username, password, confirm_password)
        val response = api.register(user)
        runCatching {
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

    suspend fun createProperty(property: CreateProperty, root: LinearLayout) {
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
                    activity.showAlertDialog(response.body()?.details.toString())
                    clearAllEditTexts(root)
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
                    activity.showAlertDialog(response.body()!!.details.toString())
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


    suspend fun addToDoList(toDoList: ToDoListTask, root: ScrollView) {
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
                    clearAllEditTexts(root)
                    activity.showAlertDialog(response.body()!!.details.toString())
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
                    activity.showAlertDialog(response.body()!!.details.toString())
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
                val myis_manager = getUserProfileDetails.is_manager

                if (myis_manager) {
                    _bothNames.postValue("$myfirstname ${mylast_name}")
                } else {
                    _bothNames.postValue("$myfirstname ${mylast_name} - Admin")
                }
                _isManager.postValue(myis_manager)

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
                    imagelist = response.body()!!.details
                    activity.makeLongToast("Image upload was successful")
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
                withContext(Dispatchers.Main) {
                    activity.showAlertDialog(response.body()?.details.toString())
                }
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

    suspend fun removeProperty(propertyId: String, rentedorowned: String) {
        runCatching {
            val response = api.deleteProperty(
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken, StringBody(propertyId)
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return
            } else {
                withContext(Dispatchers.Main) {
                    activity.showAlertDialog(response.body()?.details.toString())
                }
                CoroutineScope(Dispatchers.IO).launch() {
                    if (rentedorowned == "rented") getrentedproperties() else getOwnedproperties()
                }
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
    }


    suspend fun addOtherReceipt(otherReceiptObject: OtherReceiptsUploadObject, root: LinearLayout) {
        runCatching {
            val response = api.addOtherReceipt(
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken, otherReceiptObject
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return
            } else {
                clearAllEditTexts(root)
                withContext(Dispatchers.Main) {
                    activity.showAlertDialog(response.body()?.details.toString())
                }
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
    }


    suspend fun getOtherReceipts(othereceipt: OtherReceiptFilter) {
        runCatching {
            val response = api.getOtherReceipts(
                othereceipt.property_id, othereceipt.date_from, othereceipt.date_to, activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                val otherreceiptsList = response.body()!!.details
                _getOtherReceipts.postValue(otherreceiptsList)
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
    }


    suspend fun addRent(addRentObject: RentPaymentModel, root: LinearLayout, propertyid: String?) {
        runCatching {
            val response = api.addRentPayment(
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken, addRentObject
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return
            } else {
                getRentals(RentFilter(propertyid, "unpaid", null, null))
                withContext(Dispatchers.Main) {
                    clearAllEditTexts(root)
                    activity.showAlertDialog(response.body()?.details.toString())
                }
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
    }


    suspend fun getManagedProperties() {
        runCatching {
            val response = api.getManagedProperties(activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken)
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                val managedPropertylist = response.body()!!.details
                _listManagedProperties.postValue(managedPropertylist as MutableList<ListManagedPropertiesDetail>?)
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
    }


    suspend fun managerCheck() = runBlocking {
        runCatching {
            val response = api.getUserProfileDetails(activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken)
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                val getUserProfileDetails = response.body()!!.details
                val myis_manager = getUserProfileDetails?.is_manager
                if (myis_manager!!) {
                    activity.goToActivity(activity, ManagersPropertiesList::class.java)
                } else {
                    activity.goToActivity(activity, MainActivity::class.java)
                }
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
    }


    suspend fun getTotalExpenseOnProperty(propertyId: String, textView: TextView) {
        runCatching {
            val response = api.getTotalExpensesOnProperty(
                propertyId,
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                val total = response.body()!!.details
                textView.setText("KES ${total}")
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
    }


    suspend fun getTotalNumberofReceiptsPerHouse(propertyId: String, textView: TextView) {
        runCatching {
            val response = api.getNumberofReceiptsForHouse(
                propertyId,
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                val total = response.body()!!.details
                textView.setText(total.toString())
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
    }


    suspend fun getAllProperties() {
        runCatching {
            val response = api.getAllProperties(activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken)
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                val allproperty = response.body()!!.details
                _listallproperties.postValue(allproperty as MutableList<ListManagedPropertiesDetail>?)
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
    }


    suspend fun removeExpense(request_id: String) {
        runCatching {
            val response = api.deleteExpense(
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken, ExpenseDeleteBody(request_id)
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return
            } else {
                withContext(Dispatchers.Main) {
                    activity.showAlertDialog(response.body()?.details.toString())
                }
                activityCallback?.onDataChanged("sdfgsdf")
                getTotal()
                getTotalNumberofReceipts()
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
    }


    suspend fun deleteOtherReceipt(request_id: String) {
        runCatching {
            val response = api.deleteOtherReceipt(
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken, DeleteOtherReceiptBody(request_id)
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return
            } else {
                withContext(Dispatchers.Main) {
                    activity.showAlertDialog(response.body()?.details.toString())
                }
                activityCallback?.onDataChanged("")
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
                    _listOfTodoItems.postValue(response.body()!!.details!!)
                }
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
    }



    suspend fun getToDoListDueToday() {
        runCatching {
            val response = api.getToDoListDueToday(
                activity.getAuthDetails().authToken, activity.getAuthDetails().jwttoken
            )
            if (!response.isSuccessful) {
                val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                handleResponse(jsonObj, response.toString(), null)
                return@runCatching
            } else {
                withContext(Dispatchers.Main) {
                    _listOfTodoItemsDueToday.postValue( response.body()!!.details!!)
                }
            }
        }.onFailure {
            networkResponseFailure(it, null)
        }
    }




}