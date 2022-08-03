package paita.stream_app_final.Tafa.Activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import paita.stream_app_final.R
import paita.stream_app_final.Tafa.Authentication.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import paita.stream_app_final.Extensions.*

class MainActivity : AppCompatActivity() {

    private lateinit var formoneid: String;
    private lateinit var formtwoid: String;
    private lateinit var formthreeid: String;
    private lateinit var formfourid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initall()
    }

    private suspend fun fetchFormId(formidname: String): String {
        val formid = getFormId(myViewModel(this), formidname)
        return formid
    }

    private fun initall() {

        callTheFormIds()

        form_one.setOnClickListener {
            if (this::formoneid.isInitialized) {
                val intent = Intent(this, FormActivity::class.java)
                intent.putExtra("actualid", formoneid)
                intent.putExtra("colorname", "#B330811C")
                intent.putExtra("formname", "Form One")
                intent.putExtra("formnumber", "1")
                startActivity(intent)
            }
        }
        form_two.setOnClickListener {
            if (this::formtwoid.isInitialized) {
                val intent = Intent(this, FormActivity::class.java)
                intent.putExtra("actualid", formtwoid)
                intent.putExtra("colorname", "#5968B0")
                intent.putExtra("formname", "Form Two")
                intent.putExtra("formnumber", "2")
                startActivity(intent)
            }
        }
        form_three.setOnClickListener {
            if (this::formthreeid.isInitialized) {
                val intent = Intent(this, FormActivity::class.java)
                intent.putExtra("actualid", formthreeid)
                intent.putExtra("colorname", "#B478B5")
                intent.putExtra("formname", "Form Three")
                intent.putExtra("formnumber", "3")
                startActivity(intent)
            }
        }
        form_four.setOnClickListener {
            if (this::formfourid.isInitialized) {
                val intent = Intent(this, FormActivity::class.java)
                intent.putExtra("actualid", formfourid)
                intent.putExtra("colorname", "#E36B6B")
                intent.putExtra("formname", "Form Four")
                intent.putExtra("formnumber", "4")
                startActivity(intent)
            }
        }

        settingsImageview.setOnClickListener {

            val popup = PopupMenu(this, it)
            popup.inflate(R.menu.pop_menu)
            popup.setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener, PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(menuitem: MenuItem?): Boolean {
                    return when (menuitem!!.getItemId()) {
                        R.id.logout -> {
                            logoutUser()
                            true
                        }
                        R.id.subscriptions -> {
//                            goToActivity_Unfinished(this@MainActivity, SubscriptionActivity::class.java)
                            true
                        }
                        R.id.account -> {
                            true
                        }
                        else -> false
                    }
                }
            })
            popup.show()

        }

    }

    private fun callTheFormIds() {
        CoroutineScope(Dispatchers.IO).launch(coroutineexception(this)) {
            val oneid = async {
                fetchFormId("1")
            }
            val twoid = async {
                fetchFormId("2")
            }
            val threeid = async {
                fetchFormId("3")
            }
            val fourid = async {
                fetchFormId("4")
            }
            formoneid = oneid.await()
            formtwoid = twoid.await()
            formthreeid = threeid.await()
            formfourid = fourid.await()
        }
    }

    private fun logoutUser() {
        if (sessionManager().logout()) {
            makeLongToast("You have been logged out successfully")
            goToActivity(this, LoginActivity::class.java)
        }
    }

    private fun permissions() {
        /*
        *     <uses-permission android:name="android.permission.SEND_SMS" />
    <uses-permission
        android:name="comp.example.pk.SwiftHomeAlaucherpp.permission.MAPS_RECEIVE"
        android:required="false" />
    <uses-permission
        android:name="smartherd.hiltonsteelandcementandroid.permission.STORAGE"
        android:required="false" />
    <uses-permission
        android:name="android.permission.VIBRATE"
        android:required="false" />
    <uses-permission
        android:name="android.permission.WRITE_SETTINGS"
        android:required="false"
        tools:ignore="ProtectedPermissions" />
    <uses-permission
        android:name="android.permission.INTERNET"
        android:required="false" />
    <uses-permission
        android:name="android.permission.READ_EXTERNAL_STORAGE"
        android:required="false" />
    <uses-permission
        android:name="android.permission.ACCESS_NETWORK_STATE"
        android:required="false" />
    <uses-permission
        android:name="android.permission.ACCESS_FINE_LOCATION"
        android:required="false" />
    <uses-permission
        android:name="android.permission.ACCESS_WIFI_STATE"
        android:required="false" />
    <uses-permission
        android:name="android.permission.WAKE_LOCK"
        android:required="false" />
    <uses-permission
        android:name="android.permission.READ_PHONE_STATE"
        android:required="false" />
    <uses-permission
        android:name="android.permission.ACCESS_COARSE_LOCATION"
        android:required="false" />
    <uses-permission
        android:name="android.permission.REQUEST_INSTALL_PACKAGES"
        android:required="false" />
    <uses-permission
        android:name="android.permission.CAMERA"
        android:required="false" />
        * */
    }


    override fun onBackPressed() {
        val alert = AlertDialog.Builder(this)
            .setTitle("Tafa")
            .setCancelable(false)
            .setMessage("Are you sure you want to exit")
            .setIcon(R.drawable.tafalogo)
            .setPositiveButton("Exit", DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
                finish()
            })
            .setNegativeButton("Dismis", DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
            .show()
    }


}