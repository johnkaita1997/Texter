package com.tafatalkstudent.Shared

import android.net.Uri
import com.marwaeltayeb.progressdialog.ProgressDialog
import com.tafatalkstudent.Activities.MainActivity
import dmax.dialog.SpotsDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


object Constants {

    var gender = ""
    var isDialogShown = false
    lateinit var mydialog: SpotsDialog
    lateinit var progress: ProgressDialog
    val isprogressInitialized get() = this::progress.isInitialized
    var datemap = mutableMapOf<String, String>()
    var rentalDateMap = mutableMapOf<String, String>()
    private val TAG = MainActivity::class.java.simpleName
    var expenseImageList = mutableListOf<Uri>()
    var expenseImageUploadList = mutableListOf<String>()
    var CALL_REQUEST_CODE = 123
    val mainScope = CoroutineScope(Dispatchers.Main) // Create your own CoroutineScope
    val threadScope = CoroutineScope(Dispatchers.IO) // Create your own CoroutineScope
    const val permission_request = 100

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //              PAGINATION START
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    var baseurl = "https://tafatalk.co.ke"

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //              MPESA START
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    operator fun <T> List<T>.component6() = this[5]
    operator fun <T> List<T>.component7() = this[6]
    operator fun <T> List<T>.component8() = this[7]
    operator fun <T> List<T>.component9() = this[8]

    init {
    }


}
