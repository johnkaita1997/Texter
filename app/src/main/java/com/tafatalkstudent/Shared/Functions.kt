package com.tafatalkstudent.Shared

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import com.tafatalkstudent.Activities.LauncherActivity
import com.tafatalkstudent.R
import com.tafatalkstudent.Shared.Constants.datemap
import com.tafatalkstudent.Shared.Constants.isDialogShown
import com.tafatalkstudent.Shared.Constants.isprogressInitialized
import com.tafatalkstudent.Shared.Constants.progress
import dmax.dialog.SpotsDialog
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.coroutines.CoroutineContext


private lateinit var redirectingDialog: ProgressDialog
var alert: AlertDialog? = null


fun howtoStartActivityForResult() {
//    val makeCallLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            // Call completed successfully
//            fetchCreateCallLog()
//            showAlertDialog("A call has ended")
//        } else {
//            // Call was not completed successfully
//            showAlertDialog("Call was not completed successfully ${result.data.toString()}")
//        }
//    }
//
//    binding.call.setOnClickListener {
//        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "0793013887"))
//        startActivityForResult(intent, CALL_REQUEST_CODE)
//    }
}



fun Context.makeLongToast(message: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, duration).show()
}

fun Context.makeShortToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.goToActivity(activity: Activity, classs: Class<*>?) {
    val intent = Intent(activity, classs)
    startActivity(intent)
    activity.finish()
}

fun Context.goToActivity_Unfinished(activity: Activity, classs: Class<*>?) {
    val intent = Intent(activity, classs)
    startActivity(intent)
}

fun Context.goToactivityIntent_Unfinished(activity: Activity, classs: Class<*>?, extras: Map<String, String>) {
    val intent = Intent(activity, classs)
    extras.forEach {
        intent.putExtra("${it.key}", it.value)
    }
    activity.startActivity(intent)
}

fun Context.goToactivityIntent_Finished(activity: Activity, classs: Class<*>?, extras: Map<String, String>) {
    val intent = Intent(activity, classs)
    extras.forEach {
        intent.putExtra("${it.key}", it.value)
    }
    activity.startActivity(intent)
    activity.finish()
}

fun Context.showredirect() {
    redirectingDialog = ProgressDialog(this)
    redirectingDialog.setMessage("Redirecting...")
    redirectingDialog.show()
}

fun Context.dismissredirect() {
    if (redirectingDialog.isShowing) redirectingDialog.dismiss()
}

fun Context.showAlertDialog(message: String) {
    val alert = AlertDialog.Builder(this).setTitle("Tafa Talk").setCancelable(false).setMessage(message).setIcon(R.drawable.logodark).setPositiveButton("", DialogInterface.OnClickListener { dialog, _ ->
        isDialogShown = false
        dialog.dismiss()
    }).setNegativeButton("OKAY", DialogInterface.OnClickListener { dialog, _ ->
        dialog.dismiss()
        isDialogShown = false
    })
    if (!isDialogShown) {
        alert.show()
        isDialogShown = true
    }
}


fun Context.showAlertDialog_Special(alertDialog: AlertDialog, title: String, message: String, okaybuttonName: String, bar: () -> Unit) {
    alertDialog.setTitle(title)
    alertDialog.setIcon(R.drawable.logodark)
    alertDialog.setMessage(message)
    alertDialog.setCancelable(false)
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Dismiss") { dialog, which ->
        dialog.dismiss()
    }
    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, okaybuttonName) { dialog, which ->
        dialog.dismiss()
        bar()
    }

    if (alertDialog.isShowing) {
        alertDialog.dismiss()
    }

    alertDialog.show()

    val btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
    val btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)

    val layoutParams = btnPositive.layoutParams as LinearLayout.LayoutParams
    layoutParams.weight = 10f
    btnPositive.layoutParams = layoutParams
    btnNegative.layoutParams = layoutParams

}

fun Context.dissmissAlertDialogMessage() {
    alert?.dismiss()

}

/*
fun openYoutubeLink(youtubeID: String) {
    val intentApp = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youtubeID))
    val intentBrowser = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + youtubeID))
    try {
        this.startActivity(intentApp)
    } catch (ex: ActivityNotFoundException) {
        this.startActivity(intentBrowser)
    }
}*/


/*
class MyViewModelFactory(private val activity: Activity) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MyViewModel(activity.application, activity) as T
    }
}
*/

/*fun Context.myViewModel(activity: Activity): MyViewModel {
    return MyViewModel(activity.application, activity)
}*/

fun Context.issignedIn(): Boolean {
    val preferences: SharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
    val accesstoken = preferences.getString("access", null)
    return accesstoken != null
}


fun Context.activityisrunning(): Boolean {
    val activity = this as Activity
    return activity.getWindow().getDecorView().getRootView().isShown()
}

fun Context.myDialog(): SpotsDialog {
    val progressDialog = SpotsDialog.Builder().setContext(this).build() as SpotsDialog
    return progressDialog
}


fun Context.coroutineexception(activity: Activity): CoroutineContext {
    val handler = CoroutineExceptionHandler { _, exception ->
        activity.runOnUiThread {
            if (activityisrunning()) {
                showAlertDialog(exception.toString())
            }
            return@runOnUiThread
        }
    }
    return handler
}


fun Context.validated(edittextlist: MutableList<EditText>): Boolean {
    edittextlist.forEach {
        val edittext = it
        if (TextUtils.isEmpty(edittext.text.toString())) {
            edittext.setError("You cannot leave this field blank")
            return false
        }
    }
    return true
}


fun Context.mytext(edittext: EditText): String {
    return edittext.text.toString().trim()
}


fun Context.populateSpinner(spinner: Spinner, mymutablelistOfSpinnerItems: MutableList<String>) {
    val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mymutablelistOfSpinnerItems)
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    spinner.setAdapter(dataAdapter)
}

fun Context.dateFormatter(oldDate: String): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
    val date = formatter.parse(oldDate)
    return date.toString()
}


fun Context.dismiss(mydialog: SpotsDialog) {
    if (mydialog != null && mydialog.isShowing) {
        mydialog.dismiss()
    }
}

fun Context.showDialog(mydialog: SpotsDialog, message: String) {
    mydialog.setMessage(message)
    mydialog.show()
}



fun Context.getAuthDetails(): MyAuth {
    val access = SessionManager(this).fetchAccessToken()
    val refresh = SessionManager(this).fetchRefreshToken()
    return MyAuth(access, refresh)
}


fun Context.colorChanger(view: View, before: Int, after: Int) {
    view.setOnTouchListener(object : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if (event?.getAction() == MotionEvent.ACTION_DOWN) {
                v?.setBackgroundColor(getResources().getColor(after))
            } else if (event?.getAction() == MotionEvent.ACTION_MOVE) {
                v?.setBackgroundColor(getResources().getColor(before))
            } else if (event?.getAction() == MotionEvent.ACTION_UP) {
                v?.setBackgroundColor(getResources().getColor(before))
            }
            return true
        }
    })
}

fun Context.showProgress(activity: Activity) {
    progress =
        com.marwaeltayeb.progressdialog.ProgressDialog(activity).setDialogPadding(50).setTextSize(18F).setProgressBarColor(R.color.propdarkblue).setText("").setCancelable(false).setDialogTransparent()
    if (!progress.isShowing) {
        progress.show()
    }
}

fun Context.dismissProgress() {
    if (isprogressInitialized) {
        if (progress.isShowing) {
            progress.dismiss()
        }
    }
}


fun Context.datePicker(button: Button) {
    SingleDateAndTimePickerDialog.Builder(this)
        .bottomSheet()
        .curved()
        .titleTextColor(Color.RED)
        .displayMinutes(false)
        .displayHours(false)
        .displayDays(false)
        .displayMonth(true)
        .title("Pick A Date Below")
        .mainColor(resources!!.getColor(R.color.propdarkblue))
        .backgroundColor(Color.WHITE)
        .displayYears(true)
        .displayDaysOfMonth(true)
        .listener {
            val monthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            val thisday = (if (it.date < 10) "0" else "") + it.date
            val thismonth = monthNames.get(it.month)
            var thisyear = it.year.toString()
            if (thisyear.startsWith("1")) {
                thisyear = "20${thisyear.takeLast(2)}"
            } else {
                thisyear = "19${thisyear}"
            }
            val current = "hh:mm aaa"
            val currentTime = DateFormat.format(current, Calendar.getInstance().getTime())

            val day = thisday
            val month = thismonth
            val year = thisyear
            val time = currentTime.toString()

            var monthNumber = 0
            if (thismonth.equals("Jan")) monthNumber = 1
            else if (thismonth == "Feb") monthNumber = 2
            else if (thismonth == "Mar") monthNumber = 3
            else if (thismonth == "Apr") monthNumber = 4
            else if (thismonth == "May") monthNumber = 5
            else if (thismonth == "Jun") monthNumber = 6
            else if (thismonth == "Jul") monthNumber = 7
            else if (thismonth == "Aug") monthNumber = 8
            else if (thismonth == "Sep") monthNumber = 9
            else if (thismonth == "Oct") monthNumber = 10
            else if (thismonth == "Nov") monthNumber = 11
            else if (thismonth == "Dec") monthNumber = 12


            if (monthNumber < 10) {
                val combined = "${thisyear}-0${monthNumber}-${thisday}"
                makeLongToast("Date selected")
                val themap = mutableMapOf("day" to day, "month" to month, "year" to year, "time" to time, "combined" to combined)
                datemap = themap
                button.setText(datemap.getValue("combined"))
            } else {
                val combined = "${thisyear}-${monthNumber}-${thisday}"
                makeLongToast("Date selected")
                val themap = mutableMapOf("day" to day, "month" to month, "year" to year, "time" to time, "combined" to combined)
                datemap = themap
                button.setText(datemap.getValue("combined"))
            }


        }.display()
}

fun Context.emptyDateMap() {
    datemap.clear()
}


@OptIn(DelicateCoroutinesApi::class)
fun Context.logoutUser(studentId: String) {
    /*if (SessionManager(this).logout()) {
        makeLongToast("You have been logged out successfully")
        val database = RoomDb(this).loginDao()
        GlobalScope.launch() {
            val loggedInUser = database.getAllLogins().findLast { it.studentId == studentId && it.logoutTimestamp == null }
            if (loggedInUser != null) {
                loggedInUser.logoutTimestamp = System.currentTimeMillis()
                database.update(loggedInUser)
                Log.d("-------", "initall: User is not null")
            } else {
                Log.d("-------", "initall: User is null")
            }
        }
        goToActivity(this as Activity, LauncherActivity::class.java)
    }*/
}


fun clearAllEditTexts(parent: ViewGroup) {
    for (i in 0 until parent.childCount) {
        val child = parent.getChildAt(i)
        if (child is EditText) {
            child.setText("")
        } else if (child is ViewGroup) {
            clearAllEditTexts(child)
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun dateDifference(date1: String, date2: String): Long {
    val localDate1 = LocalDate.parse(date1)
    val localDate2 = LocalDate.parse(date2)
    return ChronoUnit.DAYS.between(localDate1, localDate2)
}


fun makeVisible(view: View) {
    view.visibility = View.VISIBLE
}

fun makeInvisible(view: View) {
    view.visibility = View.INVISIBLE
}

fun makeGone(view: View) {
    view.visibility = View.GONE
}
