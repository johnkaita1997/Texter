package com.tafatalkstudent.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleOwner
import com.tafatalkstudent.R
import com.tafatalkstudent.Shared.*
import com.tafatalkstudent.Shared.Constants.mainScope
import com.tafatalkstudent.Shared.Constants.permission_request
import com.tafatalkstudent.databinding.LauncherActivityBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LauncherActivity : AppCompatActivity(), LifecycleOwner {

    private lateinit var binding: LauncherActivityBinding
    lateinit var handler: Handler
    lateinit var runnable: Runnable
    private val viewmodel: MyViewModel by viewModels()
    private lateinit var crash: String
    var cd = ConnectionDetector(this)

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LauncherActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("ActivityName", "Current Activity: " + javaClass.simpleName)

        ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.CALL_PHONE,
                Manifest.permission.KILL_BACKGROUND_PROCESSES,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.ANSWER_PHONE_CALLS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
            ), permission_request
        )
    }

    private fun initall() {

        if (issignedIn()) {
            CoroutineScope(Dispatchers.IO).launch() {
                delay(2000)
                mainScope.launch {
                    refreshLogin()
                    goToActivity(this@LauncherActivity, LandingPage::class.java)
                }
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch() {
                delay(2000)
                mainScope.launch {
                    makeGone(binding.loadinglayout)
                    goToActivity(this@LauncherActivity, LoginActivity::class.java)
                }
            }
        }

    }

    private fun refreshLogin() {
        val u = SessionManager(this).fetchu().toString()
        val p = SessionManager(this).fetchp().toString()
        Log.d("Login-------", "initall: $u  -  $p")
        GlobalScope.launch {
            viewmodel.refreshtoken(u,p, this@LauncherActivity)
        }
    }

    private fun internet_connection_error_Dilog() {
        val alertDialog: android.app.AlertDialog? =
            android.app.AlertDialog.Builder(this).setTitle("Network Error").setMessage("This application requires an active internet connection.").setIcon(R.drawable.logodark)
                .setPositiveButton("Fix", DialogInterface.OnClickListener { _, _ -> /*  Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
        startActivityForResult(callGPSSettingIntent, 0);*/
                    val intent = Intent(Settings.ACTION_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }).setNegativeButton("Dismiss", DialogInterface.OnClickListener { _, _ ->
                finish()
                System.exit(0)
            }).show()
        val btnPositive: Button? = alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)
        val btnNegative: Button? = alertDialog?.getButton(AlertDialog.BUTTON_NEGATIVE)
        val layoutParams = btnPositive?.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 10f
        btnPositive.layoutParams = layoutParams
        btnNegative!!.layoutParams = layoutParams
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (permission_request) {
            100 -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { //Check for connectivity
                if (cd.isConnected) {
                    initall()
                } else {
                    internet_connection_error_Dilog()
                }
            }

            else -> permissionError_Dialog()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun permissionError_Dialog() {
        val alertDialog: android.app.AlertDialog? =
            android.app.AlertDialog.Builder(this).setMessage("This app might malfunction if all the permissions aren't granted.").setCancelable(false).setIcon(R.drawable.logodark).setTitle("Warning")
                .setPositiveButton("Dismiss", DialogInterface.OnClickListener { _, _ -> System.exit(0) }).setNegativeButton("", DialogInterface.OnClickListener { _, _ ->
                finish()
                System.exit(0)
            }).show()
        val btnPositive = alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)
        val layoutParams = btnPositive?.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 10f
        btnPositive.layoutParams = layoutParams
    }

    override fun onBackPressed() {
        val alert = android.app.AlertDialog.Builder(this).setTitle("Smart SMS").setCancelable(false).setMessage("Are you sure you want to exit").setIcon(R.drawable.logodark)
            .setPositiveButton("Exit", { dialog, _ ->
                dialog.dismiss()
                finish()
            }).setNegativeButton("Dismis", { dialog, _ -> dialog.dismiss() }).show()
    }

}
