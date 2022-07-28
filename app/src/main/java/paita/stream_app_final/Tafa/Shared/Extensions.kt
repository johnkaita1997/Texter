package paita.stream_app_final.Extensions

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.auth0.android.jwt.JWT
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.GsonBuilder
import dmax.dialog.SpotsDialog
import paita.stream_app_final.AppConstants.Constants
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Activities.MainActivity
import paita.stream_app_final.Tafa.Adapters.MyAuth
import paita.stream_app_final.Tafa.Adapters.VideoViewerAdapter
import paita.stream_app_final.Tafa.Adapters.Videosperunitname
import paita.stream_app_final.Tafa.Retrofit.Login.MyApi
import paita.stream_app_final.Tafa.Shared.SessionManager
import paita.stream_app_final.Tafa.Shared.ViewModel
import kotlinx.android.synthetic.main.video_bottom_sheet_layout.view.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

fun Context.showredirect() {
    redirectingDialog = ProgressDialog(this)
    redirectingDialog.setMessage("Redirecting...")
    redirectingDialog.show()
}

fun Context.dismissredirect() {
    if (redirectingDialog.isShowing) redirectingDialog.dismiss()
}

fun Context.showAlertDialog(message: String) {
    alert = AlertDialog.Builder(this).setTitle("Tafa").setCancelable(false).setMessage(message).setIcon(R.drawable.tafalogo).setPositiveButton("", DialogInterface.OnClickListener { dialog, _ ->
        dialog.dismiss()
    }).setNegativeButton("OKAY", DialogInterface.OnClickListener { dialog, _ ->
        dialog.dismiss()
    }).show()
}



fun Context.showAlertDialog_Special(alertDialog: AlertDialog, title: String, message: String, okaybuttonName: String, bar: () -> Unit) {
    alertDialog.setTitle(title)
    alertDialog.setIcon(R.drawable.tafalogo)
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


fun Context.isLoggedIn(): Boolean {
    val preferences: SharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
    val authtoken = preferences.getString("authtoken", null)
    return authtoken != null
}

fun Context.getUserId(): String {
    val sessionManager = SessionManager(this)
    val jwttoken = sessionManager.fetchJwtToken()
    Log.e("TOKEN", "getUsername: $jwttoken")
    val jwt = JWT(jwttoken.toString())
    val allClaims = jwt.claims
    val claim: String = jwt.getClaim("user").asString()!!
    return claim.toString()
}


fun Context.getUsername(): String {
    var the_Name_Of_The_User = "Not Found"
    val gson = GsonBuilder().serializeNulls().create()
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    val okHttpClient = OkHttpClient().newBuilder().addInterceptor(loggingInterceptor).build()
    val retrofit = Retrofit.Builder().baseUrl(Constants.baseurl).addConverterFactory(GsonConverterFactory.create(gson)).client(okHttpClient).build()
    val apiCall = retrofit.create(MyApi::class.java)
    val sessionManager = SessionManager(this)
    val jwttoken = sessionManager.fetchJwtToken()
    return the_Name_Of_The_User
}

fun Context.activityisrunning(): Boolean {
    val activity = this as Activity
    return activity.getWindow().getDecorView().getRootView().isShown()
}

fun Context.sessionManager(): SessionManager {
    return SessionManager(this)
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

suspend fun Context.getFormId(myviewmodel: ViewModel, form: String): String {

    val forms = myviewmodel.getForms()

    if (forms.isEmpty()) {
        withContext(Dispatchers.Main) {
            makeLongToast("Couldn't fetch forms")
        }
    } else {
        if (form == "1") {
            var theid = ""
            forms.forEach {
                if (it.name.equals("FORM ONE")) {
                    theid = it.id
                }
            }
            return theid
        } else if (form == "2") {
            var theid = ""
            forms.forEach {
                if (it.name.equals("FORM TWO")) {
                    theid = it.id
                }
            }
            return theid
        } else if (form == "3") {
            var theid = ""
            forms.forEach {
                if (it.name.equals("FORM THREE")) {
                    theid = it.id
                }
            }
            return theid
        } else if (form == "4") {
            var theid = ""
            forms.forEach {
                if (it.name.equals("FORM FOUR")) {
                    theid = it.id
                }
            }
            return theid
        } else {
            return ""
        }
    }
    return ""
}


suspend fun Context.countyid(myviewmodel: ViewModel, countyname: String): String {
    val counties = myviewmodel.getCounties()
    if (counties.isEmpty()) {
//        showAlertDialog("No counties have been added yet")
    } else {
        var theid = ""
        counties.forEach {
            if (it.name == countyname) {
                theid = it.id
            }
        }
        return theid
    }
    return ""
}


fun Context.getAuthDetails(): MyAuth {
    val authtoken = "Bearer ${SessionManager(this).fetchAuthToken()}"
    val jwttoken = "Bearer ${SessionManager(this).fetchJwtToken()}"
    return MyAuth(authtoken, jwttoken)
}


fun Context.myViewModel(activity: Activity): ViewModel {
    return ViewModel(activity.application, activity)
}


suspend fun Context.getagentid(myviewmodel: ViewModel, agentname: String): String {
    val agents = myviewmodel.getRegCodes().details
    if (agents.isEmpty()) {
//        showAlertDialog("Could not fetch agents")
    } else {
        var theid = ""
        agents.forEach {
            if (it.name == agentname) {
                theid = it.code.toString()
            }
        }
        return theid
    }
    return ""
}

fun showMpesaAlert(formid: String, alertDialog: AlertDialog, activity: Activity) {
    alertDialog.setTitle("Payment")
    alertDialog.setIcon(R.drawable.tafalogo)
    alertDialog.setMessage("You will be redicrected to Mpesa shortly. Enter your pin and confirm payment below.")
    alertDialog.setCancelable(false)
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Dismiss") { dialog, which ->
        dialog.dismiss()
    }
    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Confirm Payment") { dialog, which ->
        CoroutineScope(Dispatchers.IO).launch() {
            val isformsubscribed = activity.myViewModel(activity).isFormSubscribed(formid)
            if (!isformsubscribed) withContext(Dispatchers.Main) {
                activity.makeShortToast("Payment incomplete")
            } else {
                activity.goToActivity(activity, MainActivity::class.java)
            }
        }

        dialog.dismiss()
    }
    if (alertDialog.isShowing) alertDialog.dismiss()
    alertDialog.show()

    val btnPositive = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
    val btnNegative = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)

    val layoutParams = btnPositive.layoutParams as LinearLayout.LayoutParams
    layoutParams.weight = 10f
    btnPositive.layoutParams = layoutParams
    btnNegative.layoutParams = layoutParams
}


fun showMpesaAlert_Units(formid: String, alertDialog: AlertDialog, activity: Activity, formid1: String, unitid: String) {
    alertDialog.setTitle("Payment")
    alertDialog.setIcon(R.drawable.tafalogo)
    alertDialog.setMessage("You will be redicrected to Mpesa shortly. Enter your pin and confirm payment below.")
    alertDialog.setCancelable(false)
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Dismiss") { dialog, which ->
        dialog.dismiss()
    }
    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Confirm Payment") { dialog, which ->

//        CoroutineScope(Dispatchers.IO).launch() {
//            val isunitsubscribed = activity.myViewModel(activity).isUnitSubscribed(unitid)
//            if (!isunitsubscribed) withContext(Dispatchers.Main) {
//                activity.makeShortToast("Payment Incomplete")
//            } else {
//
//                dialog.dismiss()
//
//                withContext(Dispatchers.Main) {
//
//                    if (subunitslist.thedetails.isEmpty()) {
//                        return@withContext
//                    }
//
//                    val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//                    val sheetView: View = inflater.inflate(R.layout.video_bottom_sheet_layout, null)
//
//                    val layoutManager = LinearLayoutManager(activity)
//                    sheetView.custom_bottom_sheet_recyclerview.setLayoutManager(layoutManager)
//
//                    val mBottomSheetDialog = BottomSheetDialog(activity)
//
//                    val paymentperiodsadapter = VideoViewerAdapter(activity, subunitslist.thedetails, activity.applicationContext)
//                    sheetView.custom_bottom_sheet_recyclerview.setAdapter(paymentperiodsadapter)
//
//                    paymentperiodsadapter.notifyDataSetChanged();
//                    mBottomSheetDialog.setContentView(sheetView)
//                    mBottomSheetDialog.show()
//
//                }
//
//            }
//        }

        dialog.dismiss()
    }
    if (alertDialog.isShowing) alertDialog.dismiss()
    alertDialog.show()

    val btnPositive = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
    val btnNegative = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)

    val layoutParams = btnPositive.layoutParams as LinearLayout.LayoutParams
    layoutParams.weight = 10f
    btnPositive.layoutParams = layoutParams
    btnNegative.layoutParams = layoutParams
}

