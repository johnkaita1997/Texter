package com.propswift.Shared

import android.net.Uri
import com.marwaeltayeb.progressdialog.ProgressDialog
import com.propswift.Activities.MainActivity
import dmax.dialog.SpotsDialog


object Constants {

    var gender = ""
    var isDialogShown = false
    lateinit var mydialog: SpotsDialog
    lateinit var progress: ProgressDialog
    val isprogressInitialized get() = this::progress.isInitialized
    var datemap = mutableMapOf<String, String>()
    var rentalDateMap = mutableMapOf<String, String>()
    private val TAG = MainActivity::class.java.simpleName
    const val REQUEST_IMAGE = 100
    var expenseImageList = mutableListOf<Uri>()
    var expenseImageUploadList = mutableListOf<String>()
    var CALL_REQUEST_CODE = 123


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //              PAGINATION START
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    var baseurl = "http://192.168.100.4:8000"
//    var baseurl = "http://192.168.160.1:8000"

    //School Wifi
//    var baseurl = "http://192.168.215.28:8000"

    //Home Fiber
//    var baseurl = "http://192.168.100.10:8000"

    var baseurl = "http://192.168.0.109:8000"

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //              MPESA START
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    val busienessshortcode = "4083027"
    val thepasskey = "9cd4dd3777a83ffc18c70766a77e1f2077dbaea17188f98235158ed533f3331d"
    const val mpesa_callback_url = "http://192.168.43.105:0000"

    operator fun <T> List<T>.component6() = this[5]
    operator fun <T> List<T>.component7() = this[6]
    operator fun <T> List<T>.component8() = this[7]
    operator fun <T> List<T>.component9() = this[8]

    init {

    }


}
