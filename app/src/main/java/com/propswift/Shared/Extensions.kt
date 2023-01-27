package com.propswift.Shared

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.text.TextUtils
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.*
import com.propswift.Launchers.AddProperty
import com.propswift.Launchers.PropertyActivity
import com.propswift.Launchers.ReceiptsActivity
import com.propswift.R
import dmax.dialog.SpotsDialog
import kotlinx.coroutines.CoroutineExceptionHandler
import java.text.SimpleDateFormat
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

fun Context.goToactivityIntent_Unfinished(activity: Activity, classs: Class<*>?, extras: Map<String, String> ) {
    val intent = Intent(activity, classs)
    extras.forEach {
        intent.putExtra("${it.key}", it.value)
    }
    activity.startActivity(intent)
}

fun Context.goToactivityIntent_Finished(activity: Activity, classs: Class<*>?, extras: Map<String, String> ) {
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
    alert = AlertDialog.Builder(this).setTitle("Tafa").setCancelable(false).setMessage(message).setIcon(R.drawable.logo_small).setPositiveButton("", DialogInterface.OnClickListener { dialog, _ ->
        dialog.dismiss()
    }).setNegativeButton("OKAY", DialogInterface.OnClickListener { dialog, _ ->
        dialog.dismiss()
    }).show()
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


fun Context.myViewModel(activity: Activity): ViewModel {
    return ViewModel(activity.application, activity)
}

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

fun Context.myProgress(): ProgressDialog {
    val theProgressDialog = ProgressDialog(this)
    theProgressDialog.setTitle("Tafa Checkout")
    theProgressDialog.setMessage("Processing Payment...")
    return theProgressDialog
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

    settingsImageview.setOnClickListener {

        val popup = PopupMenu(this, it)
        popup.inflate(R.menu.pop_menu)

        popup.setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener, PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(menuitem: MenuItem?): Boolean {
                return when (menuitem!!.getItemId()) {
                    R.id.receipts -> {
                        goToActivity_Unfinished(this@settingsClick as Activity, ReceiptsActivity::class.java)
                        true
                    }
                    R.id.property -> {
                        goToActivity_Unfinished(this@settingsClick as Activity, AddProperty::class.java)
                        true
                    }
                    R.id.notes -> {
                        goToActivity_Unfinished(this@settingsClick as Activity, PropertyActivity::class.java)
                        true
                    }
                    R.id.settings -> {
                       makeLongToast("settings")
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
            } else if (event?.getAction()==MotionEvent.ACTION_MOVE){
                v?.setBackgroundColor(getResources().getColor(before))
            }
            else if (event?.getAction() == MotionEvent.ACTION_UP) {
                v?.setBackgroundColor(getResources().getColor(before))
            }
            return true
        }
    })
}