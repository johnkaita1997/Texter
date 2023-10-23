package com.tafatalkstudent.Shared

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.*
import com.tafatalkstudent.Activities.LauncherActivity
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

    val _isParentAccountActive = MutableLiveData<Boolean?>()
    val isParentAccountActive: LiveData<Boolean?> get() = _isParentAccountActive

    val _bothNames = MutableLiveData<String>()
    val bothNames: LiveData<String> get() = _bothNames

    val _totalAmount = MutableLiveData<String>()
    val totalAmount: LiveData<String> get() = _totalAmount

    val _getTotalNumberofReceipts = MutableLiveData<String>()
    val getTotalNumberofReceipts: LiveData<String> get() = _getTotalNumberofReceipts


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

    private fun saveLoginSessionToStudentId(studentid: Int, activity: Activity) {
        /*val login = Login(studentId = studentid.toString(), loginTimestamp = System.currentTimeMillis(), logoutTimestamp = null)
        CoroutineScope(Dispatchers.IO).launch() {
            val database = RoomDb(activity).loginDao().insert(login)
        }*/
    }


    suspend fun insertSmsDetail(smsDetail: SmsDetail, activity: Activity): SmsDetail {
        val database = RoomDb(activity).getSmsDao()
        var insertedId = ""
        insertedId = database.insertSmsDetail(smsDetail).toString()
        Log.d("Mychek-------", "initall: checking for ${insertedId}")
        return database.getSmsDetailByTimestamp(insertedId.toLong()) // Assuming you have a function to retrieve SmsDetail by ID
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
                val message = it.message ?: "Unknown error occurred"
                Log.d("FailedPut-", "initall: Failed inserting batch ${it.message}")
                // Optionally, you can introduce a delay before retrying
                delay(500L)
            }
        }

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


    suspend fun getTotalSmsDetailCount(activity: Activity): Int {
        return withContext(Dispatchers.IO) {
            val database = RoomDb(activity).getSmsDao()
            database.getTotalSmsDetailCount()
        }
    }

    suspend fun getDraftSmsCount(activity: Activity): Int {
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


    suspend fun markMessagesAsRead(phoneNumber: String, activity: Activity): Boolean {
        return try {
            val database = RoomDb(activity).getSmsDao()
            database.markMessagesAsRead(phoneNumber)
            true
        } catch (e: Exception) {
            // Handle exceptions if needed
            false
        }
    }


    suspend fun updateMembers(groupId: Long, membersToAdd: List<Contact>, membersToRemove: List<Contact>, activity: Activity) {
        val database = RoomDb(activity).getSmsDao()
        val group = database.getGroupById(groupId)
        val existingMembers = group.members.toMutableList()

        // Add new members
        for (newMember in membersToAdd) {
            if (!existingMembers.contains(newMember)) {
                existingMembers.add(newMember)
            }
        }

        // Remove members
        existingMembers.removeAll(membersToRemove)

        val updatedGroup = group.copy(members = existingMembers)
        database.updateGroup(updatedGroup)
    }


    suspend fun insertGroup(group: Groups, activity: Activity): Boolean {
        val database = RoomDb(activity).getSmsDao()
        return try {
            database.insertGroup(group)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateGroup(group: Groups, activity: Activity) {
        val database = RoomDb(activity).getSmsDao()
        database.updateGroup(group)
    }

    suspend fun deleteGroup(groupId: Long, activity: Activity) {
        val database = RoomDb(activity).getSmsDao()
        database.deleteGroupById(groupId)
    }

    suspend fun getAllGroups(activity: Activity): MutableList<Groups> {
        val database = RoomDb(activity).getSmsDao()
        return database.getAllGroups()
    }


    suspend fun getGroupById(groupId: Long, activity: Activity): Groups? {
        val database = RoomDb(activity).getSmsDao()
        return try {
            database.getGroupById(groupId)
        } catch (e: Exception) {
            null
        }
    }


    suspend fun insertGroupSmsDetail(smsDetail: GroupSmsDetail, activity: Activity): GroupSmsDetail {
        val database = RoomDb(activity).getSmsDao()
        var insertedId = ""
        insertedId = database.insertGroupSmsDetail(smsDetail).toString()
        Log.d("Mychek-------", "initall: checking for ${insertedId}")
        return database.getGroupSmsDetailByTimestamp(insertedId.toLong()) // Assuming you have a function to retrieve SmsDetail by ID
    }


    suspend fun deleteGroupMessageByTimestamp(timestamp: Long, activity: Activity) {
        val database = RoomDb(activity).getSmsDao()
        database.deleteGroupMessageByTimestamp(timestamp)
    }

    suspend fun deleteGroupMessageByTimestampAndDraft(timestamp: Long, activity: Activity) {
        val database = RoomDb(activity).getSmsDao()
        database.deleteGroupMessageByTimestampAndDraft(timestamp)
    }


    suspend fun markAllGroupMessagesAsRead(groupId: Long, activity: Activity) {
        val database = RoomDb(activity).getSmsDao()
        database.markAllGroupMessagesAsRead(groupId)
    }


    suspend fun getLatestGroupDraftMessage(activity: Activity): GroupSmsDetail? {
        val database = RoomDb(activity).getSmsDao()
        return database.getLatestGroupDraftMessage()
    }


    suspend fun getGroupSmsDetailById(groupId: Long, activity: Activity): MutableList<GroupSmsDetail> {
        val database = RoomDb(activity).getSmsDao()
        val getGroupSmsDetailById = database.getGroupSmsDetailById(groupId)
        return getGroupSmsDetailById
    }

    suspend fun getAllGroupSmsDetails(activity: Activity): MutableList<GroupSmsDetail> {
        val database = RoomDb(activity).getSmsDao()
        val getGroupSmsDetailById = database.getAllGroupSmsDetails()
        return getGroupSmsDetailById
    }

    suspend fun getAllSmsDetails(activity: Activity): MutableList<SmsDetail> {
        val database = RoomDb(activity).getSmsDao()
        val getAllSmsDetails = database.getAllSmsDetails()
        return getAllSmsDetails
    }


    suspend fun getGroupSmsDetailByIdUniqueCodeStamp(groupId: Long, activity: Activity): MutableList<GroupSmsDetail> {
        val database = RoomDb(activity).getSmsDao()
        val getGroupSmsDetailById = database.getGroupSmsDetailByIdUniqueCodeStamp(groupId)
        return getGroupSmsDetailById
    }

    suspend fun getLatestGroupMessage(groupId: Long, activity: Activity): GroupSmsDetail? {
        val database = RoomDb(activity).getSmsDao()
        val draftMessage = database.getSpecificTimestampGroupMessage(groupId)
        if (draftMessage != null) {
            return draftMessage
        } else {
            val getGroupSmsDetailById = database.getLatestGroupMessage(groupId)
            return getGroupSmsDetailById
        }
    }

    suspend fun getFailedGroupMessages(activity: Activity): List<GroupSmsDetail> {
        val database = RoomDb(activity).getSmsDao()
        val failedGroupMessages = database.getFailedGroupMessages()
        return failedGroupMessages
    }


}