package com.propswift.Shared

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.text.TextUtils
import android.text.format.DateFormat
import android.view.*
import android.widget.*
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.propswift.Activities.WelcomeOneActivity
import com.propswift.Expenses.ViewExpensesActivity
import com.propswift.Managers.View.ManagersActivity
import com.propswift.Property.AddProperty.AddPropertyActivity
import com.propswift.Property.ListProperties.AddExpensesActivity
import com.propswift.Property.ListProperties.PropertyFetchParentActivity
import com.propswift.R
import com.propswift.Receipts.Add.OtherReceipt.AddOtherReceiptsActivity
import com.propswift.Receipts.ReceiptsParentActivity
import com.propswift.Retrofit.MyApi
import com.propswift.Shared.Constants.datemap
import com.propswift.Shared.Constants.isDialogShown
import com.propswift.Shared.Constants.isprogressInitialized
import com.propswift.Shared.Constants.progress
import com.propswift.ToDoList.ToDoListActivity
import com.propswift.databinding.BottomExpensesBinding
import com.propswift.databinding.BottomPropertyBinding
import com.propswift.databinding.BottomReceiptsBinding
import dmax.dialog.SpotsDialog
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext


private lateinit var redirectingDialog: ProgressDialog
var alert: AlertDialog? = null


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
    val alert = AlertDialog.Builder(this).setTitle("PropSwift").setCancelable(false).setMessage(message).setIcon(R.drawable.logo_small).setPositiveButton("", DialogInterface.OnClickListener { dialog, _ ->
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
    alertDialog.setIcon(R.drawable.logo_small)
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

fun Context.isLoggedIn(): Boolean {
    val preferences: SharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
    val authtoken = preferences.getString("authtoken", null)
    return authtoken != null
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


fun Context.settingsClick(settingsImageview: View) {

    val popup = PopupMenu(this, settingsImageview)
    popup.inflate(R.menu.pop_menu)

    CoroutineScope(Dispatchers.IO).launch() {
        runCatching {
            val response = MyApi().getUserProfileDetails(getAuthDetails().authToken, getAuthDetails().jwttoken)
            if (!response.isSuccessful) {
                return@runCatching
            } else {
                val getUserProfileDetails = response.body()!!.details

                val myis_manager = getUserProfileDetails!!.is_manager
                if (myis_manager) {
                    popup.menu.findItem(R.id.managers).setEnabled(false)
                    popup.menu.findItem(R.id.property).setEnabled(false)
                }
            }
        }
    }

    settingsImageview.setOnClickListener {

        popup.setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener, PopupMenu.OnMenuItemClickListener {

            val expensesbinding = BottomExpensesBinding.inflate(LayoutInflater.from(this@settingsClick))
            val receiptbinding = BottomReceiptsBinding.inflate(LayoutInflater.from(this@settingsClick))
            val propertybinding = BottomPropertyBinding.inflate(LayoutInflater.from(this@settingsClick))

            init {
                CoroutineScope(Dispatchers.IO).launch() {
                    runCatching {
                        val response = MyApi().getUserProfileDetails(getAuthDetails().authToken, getAuthDetails().jwttoken)
                        if (!response.isSuccessful) {
                            return@runCatching
                        } else {
                            val getUserProfileDetails = response.body()!!.details

                            val myis_manager = getUserProfileDetails!!.is_manager
                            if (myis_manager) {
                                receiptbinding.viewreceipts.visibility = View.GONE
                                expensesbinding.viewexpenses.visibility = View.GONE
                            }
                        }
                    }
                }
            }

            override fun onMenuItemClick(menuitem: MenuItem): Boolean {

                return when (menuitem.getItemId()) {

                    R.id.property -> {
                        val bottomSheetDialog = BottomSheetDialog(this@settingsClick)
                        bottomSheetDialog.setContentView(propertybinding.root)

                        propertybinding.createnewproperty.setOnClickListener {
                            goToactivityIntent_Unfinished(this@settingsClick as Activity, AddPropertyActivity::class.java, mapOf("operation" to "createproperty"))
                        }
                        propertybinding.viewProperties.setOnClickListener {
                            goToActivity_Unfinished(this@settingsClick as Activity, PropertyFetchParentActivity::class.java)
                        }
                        bottomSheetDialog.show()
                        true
                    }


                    R.id.receipts -> {
                        val bottomSheetDialog = BottomSheetDialog(this@settingsClick)
                        bottomSheetDialog.setContentView(receiptbinding.root)

                        receiptbinding.createreceipt.setOnClickListener {
                            goToactivityIntent_Unfinished(this@settingsClick as Activity, AddOtherReceiptsActivity::class.java, mapOf("operation" to "createproperty"))
                        }
                        receiptbinding.viewreceipts.setOnClickListener {
                            goToActivity_Unfinished(this@settingsClick as Activity, ReceiptsParentActivity::class.java)
                        }
                        bottomSheetDialog.show()
                        true
                    }


                    R.id.expenses -> {
                        val bottomSheetDialog = BottomSheetDialog(this@settingsClick)
                        bottomSheetDialog.setContentView(expensesbinding.root)

                        expensesbinding.createexpense.setOnClickListener {
                            goToactivityIntent_Unfinished(this@settingsClick as Activity, AddExpensesActivity::class.java, mapOf("operation" to "createexpense"))
                        }
                        expensesbinding.viewexpenses.setOnClickListener {
                            goToActivity_Unfinished(this@settingsClick as Activity, ViewExpensesActivity::class.java)
                        }
                        bottomSheetDialog.show()
                        true
                    }


                    R.id.managers -> {
                        goToActivity_Unfinished(this@settingsClick as Activity, ManagersActivity::class.java)
                        true
                    }

                    R.id.todoList -> {
                        goToActivity_Unfinished(this@settingsClick as Activity, ToDoListActivity::class.java)
                        true
                    }

                    R.id.logout -> {
                        logoutUser()
                        true
                    }

                    else -> false
                }
            }
        })
        popup.show()

    }
}


fun Context.getAuthDetails(): MyAuth {
    val authtoken = "${SessionManager(this).fetchAuthToken()}"
    val jwttoken = "${SessionManager(this).fetchJwtToken()}"
    return MyAuth(authtoken, jwttoken)
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


fun Context.logoutUser() {
    if (SessionManager(this).logout()) {
        makeLongToast("You have been logged out successfully")
        goToActivity(this as Activity, WelcomeOneActivity::class.java)
    }

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