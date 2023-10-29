package com.tafatalkstudent.Shared

import android.net.Uri
import android.nfc.tech.MifareUltralight.PAGE_SIZE
import androidx.paging.PagingConfig
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

    val pagingConfig = PagingConfig(
        pageSize = 20, // Define your page size here
        enablePlaceholders = true,
        prefetchDistance = 20,
        initialLoadSize = 20
    //initialLoadSize = PAGE_SIZE * 3 // You can adjust initial load size based on your requirement
    )


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //              PAGINATION START
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    var baseurl = "http://20.102.106.83:2001"

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
